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
└── trophix-api/                # Spring Boot 4.0.7 / Java 25 / Maven (porta 8080)
    └── src/main/java/com/trophix/api/
        ├── auth/               # JWT em cookie HttpOnly + RBAC + seed de admin
        ├── users/              # vinculo PSN, perfil, jogos, sync de perfil
        ├── games/              # catalogo de jogos + progresso do usuario
        ├── trophies/           # catalogo de trofeus + conquistas do usuario
        ├── guides/             # guias (roadmaps + dicas) com upvotes
        └── shared/             # UUIDv7, exceptions PT-BR, security, RestClient
```

**Padrões:** Clean Architecture / Hexagonal em cada módulo (`model` / `application` com ports+usecases / `infrastructure` com adapters), Código em inglês + mensagens de retorno em Português-BR, UUIDv7 nas PKs, Flyway para migrações, 12-Factor (credenciais via `.env`).

## Pré-requisitos

- Docker + Docker Compose
- Node.js 20+ (só para rodar o sidecar fora do container)
- JDK 25 + Maven (só para desenvolver o trophix-api)

## Subir tudo (recomendado)

```bash
cp .env.example .env      # edite e cole seu NPSSO_TOKEN real
docker compose up -d --build
```

| Serviço       | Container           | Porta            | Descrição |
| ------------- | ------------------- | ---------------- | --------- |
| `postgres`    | `trophix-postgres`  | `localhost:5432` | PostgreSQL 18, volume `postgres-data` |
| `psn-sidecar` | `trophix-psn-sidecar` | `localhost:3000` | Autentica na PSN e expõe os dados do jogador |

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

## Variáveis de ambiente

### `.env` da raiz (docker-compose)

| Variável        | Obrigatória | Descrição |
| --------------- | ----------- | --------- |
| `POSTGRES_DB`     | não (default `trophix`) | Nome do banco |
| `POSTGRES_USER`   | não (default `trophix`) | Usuário do banco |
| `POSTGRES_PASSWORD` | não (default `trophix`) | Senha do banco |
| `NPSSO_TOKEN`     | **sim**     | Token PSN de 64 caracteres |

> NPSSO: obtenha em `https://ca.account.sony.com/api/v1/ssocookie` logado na PSN. Vale como senha e expira (~60 dias); o sidecar renova access/refresh sozinho depois do boot.

### `.env` do trophix-api (Spring)

| Variável     | Descrição |
| ------------ | --------- |
| `POSTGRES_URL` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Conexão com o Postgres |
| `JWT_SECRET` | Segredo do JWT em Base64 (≥ 32 bytes) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `ADMIN_PSN_ID` | Seed do admin inicial (dev tem defaults) |

## Endpoints do sidecar (porta 3000)

| Rota | Descrição |
| ---- | --------- |
| `GET /api/perfil/:psnId` | Perfil público (psnId, aboutMe, avatarUrl) |
| `GET /api/jogos/:npCommunicationId/trofeus` | Catálogo de troféus do jogo |
| `GET /api/jogos/:npCommunicationId/trofeus-conquistados/:accountId` | Status de conquista de cada troféu |
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
| POST | `/api/users/me/sync` | autenticado | Sincroniza nível/totais + jogos (via sidecar) |
| GET | `/api/users/{username}/profile` | público | Perfil (nível, progresso, totais) |
| GET | `/api/users/{username}/games` | público | Jogos paginados (ordenados por última jogada) |
| GET | `/api/users/{userId}` | autenticado | Dados do usuário (inclui e-mail) |

### Games / Troféus
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/games/{gameId}/sync-trophies` | autenticado | Sincroniza catálogo de troféus + conquistas do usuário |
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
3. **Sync** — perfil (nível/totais + jogos) e por jogo (catálogo de troféus + conquistas com data).
4. **Guias** — roadmaps por jogo e dicas por troféu, submetidos com status `PENDING`, moderados por admin, com votação anti-fraude (contador atômico + `UNIQUE (guide_id, user_id)`).
5. **Consulta pública** — dica de chefe via `GET /api/trophies/{trophyId}/guides`; roadmap de 40h via `GET /api/games/np/{npCommunicationId}/guides`.

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
