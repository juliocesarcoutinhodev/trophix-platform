# Trophix Platform

Monorepo do ecossistema Trophix: plataforma de guias de troféus da PSN, com autenticação por cookie JWT, sincronização de perfis/jogos/troféus via um sidecar Node.js, e sistema de guias (roadmaps + dicas) com votação anti-fraude.

## Arquitetura

```
trophix-platform/
├── docker-compose.yml          # orquestra banco + sidecar (1 comando)
├── .env.example                # documentacao das variaveis (ficticio)
├── .env                        # variaveis reais locais (NAO versionar)
├── README.md
├── trophix-psn-sidecar/        # Node 20 + Express + psn-api (ponte PSN, porta 3000)
├── trophix-api/                # Spring Boot 4.0.7 / Java 25 / Maven (porta 8080)
│   └── src/main/java/com/trophix/api/
│       ├── auth/               # JWT em cookie HttpOnly + RBAC + seed de admin
│       ├── users/              # vinculo PSN, perfil, jogos, sync de perfil
│       ├── games/              # catalogo de jogos + detalhe (progresso/raridades)
│       ├── trophies/           # catalogo de trofeus + conquistas do usuario
│       ├── guides/             # guias (roadmaps + dicas) com upvotes
│       └── shared/             # UUIDv7, exceptions PT-BR, security, RestClient
└── trophix-web/                # Angular 22 standalone + Tailwind v4 (porta 4200)
    └── src/app/
        ├── core/               # services (Api/Auth), guard, interceptor, pipes, models
        ├── layout/             # navbar
        └── pages/              # dashboard, login, register, game-detail, coming-soon
```

**Padrões:** Clean Architecture / Hexagonal em cada módulo (`model` / `application` com ports+usecases / `infrastructure` com adapters), Código em inglês + mensagens de retorno em Português-BR, UUIDv7 nas PKs, Flyway para migrações, 12-Factor (credenciais via `.env`). No front: components standalone, signals, tema dark e Tailwind CSS v4.

## Pré-requisitos

- Docker + Docker Compose
- Node.js 20+ (sidecar e front-end)
- JDK 25 + Maven (só para desenvolver o trophix-api)

## Subir infraestrutura (banco + sidecar)

```bash
cp .env.example .env      # edite e cole seu NPSSO_TOKEN real
docker compose up -d --build
```

| Serviço       | Container           | Porta            | Descrição |
| ------------- | ------------------- | ---------------- | --------- |
| `postgres`    | `trophix-postgres`  | `localhost:5432` | PostgreSQL 18, volume `postgres-data` |
| `psn-sidecar` | `trophix-psn-sidecar` | `localhost:3000` | Autentica na PSN e expõe os dados do jogador |

O sidecar faz **cache em memória** das respostas de troféus (TTL configurável via `TROFEUS_CACHE_TTL_MS`, default 10 min), então a PSN não é consultada a cada requisição — o que deixa barato o sync automático da página de detalhes do jogo.

Acompanhe o boot do sidecar (valida o NPSSO e renova tokens sozinho):

```bash
docker compose logs -f psn-sidecar
```

## Rodar a API Java (dev)

```bash
cd trophix-api
mvn spring-boot:run          # profile dev, lê o .env da própria pasta
# na inicialização: Flyway migra o schema, seed de roles e seed de admin
```

Admin padrão (dev): `admin@trophix.com` / `admin123` (override por `ADMIN_EMAIL`/`ADMIN_PASSWORD`/`ADMIN_PSN_ID`). Em produção o admin só é criado se essas variáveis forem definidas no ambiente.

## Rodar o front-end (trophix-web)

```bash
cd trophix-web
npm install
npm start                    # http://localhost:4200
```

O dev server usa um proxy (`proxy.conf.json`) que encaminha `/api` para `http://localhost:8080`. Em produção o front deve ser servido no mesmo origin da API (coerente com o cookie `SameSite=Strict`).

## Resiliência do sidecar

A API consulta o sidecar via `RestClient` com **timeouts** e um **circuit breaker** em memória (CLOSED → OPEN → HALF_OPEN), centralizados no `shared.infrastructure.web.SidecarClient`:

| Config (`application.yml`) | Default | Descrição |
| -------------------------- | ------- | --------- |
| `trophix.sidecar.connect-timeout` | `2s` | Timeout de conexão |
| `trophix.sidecar.read-timeout` | `5s` | Timeout de leitura |
| `trophix.sidecar.circuit-breaker.failure-threshold` | `5` | Falhas consecutivas para abrir o circuito |
| `trophix.sidecar.circuit-breaker.open-timeout` | `30s` | Tempo em OPEN antes de permitir probes |
| `trophix.sidecar.circuit-breaker.half-open-max-calls` | `3` | Probes no estado meio-aberto |

Quando o sidecar está fora, as operações de sync falham rápido (502 enquanto o circuito está fechado, 503 quando ele abre) em vez de pendurar threads; os endpoints de leitura continuam servindo os dados já persistidos no Postgres. 404s legítimos (ex.: usuário inexistente na PSN) não abrem o circuito.

## Variáveis de ambiente

### `.env` da raiz (docker-compose)

| Variável        | Obrigatória | Descrição |
| --------------- | ----------- | --------- |
| `POSTGRES_DB`     | não (default `trophix`) | Nome do banco |
| `POSTGRES_USER`   | não (default `trophix`) | Usuário do banco |
| `POSTGRES_PASSWORD` | não (default `trophix`) | Senha do banco |
| `NPSSO_TOKEN`     | **sim**     | Token PSN de 64 caracteres |
| `TROFEUS_CACHE_TTL_MS` | não (default `600000`) | Cache do sidecar p/ troféus (ms) |

> NPSSO: obtenha em `https://ca.account.sony.com/api/v1/ssocookie` logado na PSN. Vale como senha e expira (~60 dias); o sidecar renova access/refresh sozinho depois do boot.

### `.env` do trophix-api (Spring)

| Variável     | Descrição |
| ------------ | --------- |
| `POSTGRES_URL` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Conexão com o Postgres |
| `JWT_SECRET` | Segredo do JWT em Base64 (≥ 32 bytes) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `ADMIN_PSN_ID` | Seed do admin inicial (dev tem defaults) |
| `TROPHIX_CORS_ORIGIN` | Origem CORS permitida (prod; dev usa `http://localhost:4200`) |
| `TROPHIX_SIDECAR_BASEURL` | URL do sidecar (opcional; default `http://localhost:3000`) |

## Endpoints do sidecar (porta 3000)

| Rota | Descrição |
| ---- | --------- |
| `GET /api/perfil/:psnId` | Perfil público (psnId, aboutMe, avatarUrl) |
| `GET /api/jogos/:npCommunicationId/trofeus` | Catálogo de troféus do jogo (com cache) |
| `GET /api/jogos/:npCommunicationId/trofeus-conquistados/:accountId` | Status de conquista de cada troféu (com cache) |
| `GET /api/resumo/:psnId` | Nível, progresso e totais do jogador |
| `GET /api/jogos-usuario/:accountId` | Histórico de jogos com progresso e troféus |

## Endpoints da API (porta 8080)

### Auth
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/auth/register-completion` | público | Finaliza cadastro (email/senha/role, busca avatar na PSN) |
| POST | `/api/auth/login` | público | Login — devolve JWT em cookie HttpOnly |
| POST | `/api/auth/logout` | autenticado | Invalida o cookie |

### Usuários / vínculo PSN
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/users/link-request` | público | Gera token `TRFX-XXXX` (15 min) p/ pôr no About Me |
| POST | `/api/users/link-validate` | público | Valida o token no perfil e consome o ticket |
| POST | `/api/users/me/sync` | autenticado | Sincroniza nível/totais + jogos (assíncrono, via sidecar) |
| GET | `/api/users/me/profile` | autenticado | Perfil do usuário logado |
| GET | `/api/users/me/games` | autenticado | Jogos do usuário logado (paginado) |
| GET | `/api/users/{username}/profile` | público | Perfil público (nível, progresso, totais) |
| GET | `/api/users/{username}/games` | público | Jogos públicos paginados (ordenados por última jogada) |
| GET | `/api/users/{userId}` | autenticado | Dados do usuário (inclui e-mail) |

### Games / Troféus
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/games/{gameId}/sync-trophies` | autenticado | Sincroniza catálogo de troféus + conquistas do usuário |
| GET | `/api/games/{gameId}/detail` | autenticado | Detalhe do jogo p/ o usuário logado (progresso + contagem por raridade) |
| GET | `/api/games/{gameId}/my-trophies` | autenticado | Catálogo do jogo com status de conquista (earned/earnedAt) |
| GET | `/api/games/{gameId}/trophies` | público | Lista os troféus do jogo (com o UUID interno) |

### Guias
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/games/{gameId}/guides` | autenticado | Submete roadmap do jogo (PENDING) |
| POST | `/api/trophies/{trophyId}/guides` | autenticado | Submete dica de troféu (PENDING) |
| PATCH | `/api/guides/{guideId}/review?action=APPROVE|REJECT` | **ROLE_ADMIN** | Modera o guia |
| POST | `/api/guides/{guideId}/vote` | autenticado | Vota/desvota (toggle, 1 voto por usuário) |
| GET | `/api/trophies/{trophyId}/guides` | público | Dicas aprovadas daquele troféu |
| GET | `/api/games/np/{npCommunicationId}/guides` | público | Roadmaps aprovados daquele jogo |

## Fluxo do produto

1. **Vínculo da conta PSN** — `link-request` gera `TRFX-XXXX`; o jogador coloca no *About Me*; `link-validate` confere a propriedade.
2. **Cadastro** — email + senha + role `ROLE_USER`; avatar buscado na PSN.
3. **Sync do perfil** — `POST /api/users/me/sync` atualiza nível/totais e o histórico de jogos (`user_games`), de forma assíncrona (202), com cooldown de 15 min e agendamento diário às 04:00.
4. **Sync de troféus por jogo** — `POST /api/games/{gameId}/sync-trophies` persiste o catálogo (`trophies`) e as conquistas com data (`user_trophies`). O sidecar serve respostas com cache, e a página de detalhes dispara esse sync automaticamente a cada visita.
5. **Detalhes do jogo** — a página `/jogos/:id` mostra capa, plataforma, progresso e a contagem de Platina/Ouro/Prata/Bronze conquistadas (`GET /api/games/{gameId}/detail`), além da lista de troféus com `earned`/`earnedAt` (`GET /api/games/{gameId}/my-trophies`). O sync silencioso mantém os dados atualizados sem ação do usuário.
6. **Guias** — roadmaps por jogo e dicas por troféu, submetidos com status `PENDING`, moderados por admin, com votação anti-fraude (contador atômico + `UNIQUE (guide_id, user_id)`).
7. **Consulta pública** — dica de chefe via `GET /api/trophies/{trophyId}/guides`; roadmap de 40h via `GET /api/games/np/{npCommunicationId}/guides`.

## Collections Postman

- `trophix-api/trophix-api.postman_collection.json` — todos os endpoints da API (pastas Auth, Usuários, Games, Guias)
- `trophix-psn-sidecar/trophix-psn-sidecar.postman_collection.json` — endpoints do sidecar

O login guarda o cookie no Cookie Jar do Postman automaticamente; as rotas protegidas só funcionam após o Login.

## Segurança

- `.env`/`.env.*` no `.gitignore`; só o `.env.example` é versionado.
- `NPSSO_TOKEN` e `JWT_SECRET` valem como senhas — nunca em logs, issues ou commits.
- JWT só via cookie `HttpOnly; SameSite=Strict; Secure` (prod) — nunca no corpo da resposta.
- RBAC em tabelas (`roles`, `user_roles`); moderação exige `ROLE_ADMIN`; voto único garantido por constraint no banco.
- `docker compose config` mostra as variáveis resolvidas antes de subir.
