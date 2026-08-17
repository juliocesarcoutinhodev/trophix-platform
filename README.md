# Trophix Platform

Monorepo do ecossistema Trophix: plataforma de guias de troféus da PSN, com autenticação por cookies HttpOnly (JWT de acesso curto + refresh token opaco com rotação e detecção de reuso), sincronização de perfis/jogos/troféus via um sidecar Node.js, e sistema de guias (roadmaps + dicas) com votação anti-fraude.

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
│       ├── reports/            # denúncias de conteúdo (GUIAS/USUÁRIOS/COMENTÁRIOS)
│       ├── forums/             # fórum (categorias, tópicos e respostas)
│       ├── admin/              # painel admin (stats, usuários, moderação)
│       ├── settings/           # configurações globais (single-row)
│       └── shared/             # UUIDv7, exceptions PT-BR, security, storage (MinIO), RestClient
└── trophix-web/                # Angular 22 standalone + Tailwind v4 (porta 4200)
    └── src/app/
        ├── core/               # services (Api/Auth), guard, interceptor, pipes, models
        ├── layout/             # navbar
        └── pages/              # dashboard, login, register, game-detail, coming-soon
```

**Padrões:** Clean Architecture / Hexagonal em cada módulo (`model` / `application` com ports+usecases / `infrastructure` com adapters), Código em inglês + mensagens de retorno em Português-BR, UUIDv7 nas PKs, Flyway para migrações, 12-Factor (credenciais via `.env`). Conversões DTO → Command e Domain → Response via **MapStruct** (`@Mapper(componentModel = "spring")`) para manter os controllers limpos (apenas orquestram `mapper.toCommand()` → `useCase` → `mapper.toResponse()`). No front: components standalone, signals, tema dark e Tailwind CSS v4.

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
| `rabbitmq`    | `trophix-rabbitmq`  | `5672` (AMQP) / `15672` (console) | Fila de sincronização assíncrona |
| `psn-sidecar` | `trophix-psn-sidecar` | `localhost:3000` | Autentica na PSN e expõe os dados do jogador |
| `mailpit`     | `trophix-mailpit`   | `1025` (SMTP) / `8025` (UI) | Servidor de e-mail falso p/ testes (dev) |
| `minio`       | `trophix-minio`     | `9000` (API S3) / `9001` (console) | Armazenamento de objetos (imagens de guias/avatares) |

Console do RabbitMQ: `http://localhost:15672` (`trophix`/`trophix`, configurável por `RABBITMQ_USER`/`RABBITMQ_PASSWORD`).

E-mails transacionais (ex.: redefinição de senha) são enviados no dev pelo **Mailpit** — a UI em `http://localhost:8025` mostra as mensagens recebidas (SMTP em `localhost:1025`, sem autenticação).

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

## Rate limiting

Filtro token-bucket na cadeia do Spring Security (antes da autenticação), por IP do cliente + grupo de rota:

| Grupo | Rotas | Limite (capacidade / refill) |
| ----- | ----- | ---------------------------- |
| `auth` | `POST /api/auth/login`, `/register-completion`, `/refresh`, `/logout`, `/api/users/link-request`, `/link-validate` | 10 / 20 por min (anti força bruta) |
| `public-read` | `GET /api/users/*/profile`, `/api/users/*/games`, `/api/trophies/*/guides`, `/api/games/np/*/guides`, `/api/games/*/trophies` | 60 / 120 por min (anti scraper) |
| `default` | demais rotas `/api` (autenticadas) | 300 / 600 por min |

Excesso responde `429` com corpo PT-BR e header `Retry-After`. Configurável em `trophix.rate-limit.*` (`application.yml`), incluindo `enabled` e `trust-forwarded-header` (usar quando houver proxy atrás). O limiter é em memória (por instância) — para múltiplas instâncias, migrar para Redis ou aplicar no gateway (Nginx) no edge.

## Sessões com refresh token rotacionado

Autenticação em dois níveis, seguindo as recomendações do OWASP e o modelo de rotação da Auth0 / Microsoft Entra:

- **Access token** — JWT curto (`trophix.jwt.expiration`, default `PT1H`), sem estado, transportado no cookie `trophix_jwt` (`HttpOnly; SameSite=Strict; Path=/`).
- **Refresh token** — valor **opaco** de 256 bits (CSPRNG) no cookie `trophix_refresh` (`HttpOnly; SameSite=Strict; Path=/api/auth`, viajando apenas nas rotas de auth). **Só o hash SHA-256 é persistido** na tabela `refresh_tokens`; o valor em claro nunca toca o banco.

```
login ──► access JWT (1h) + refresh opaco (30 dias)     [2 cookies HttpOnly]
  │
  ▼  (access expira)
POST /api/auth/refresh ──► rotaciona: revoga o refresh usado e emite novo par
  │                          na mesma família (TTL absoluto é renovado)
  ▼
logout ──► revoga a família inteira no servidor + limpa os 2 cookies
```

- **Rotação:** cada uso do refresh emite um novo par e invalida o anterior de forma atômica (transação + lock pessimista na linha), dentro da mesma **família** (`family_id`). Uma cadeia de rotações pode ser usada por todo o TTL.
- **Detecção de reuso (anti-roubo):** apresentar um refresh **já rotacionado** indica vazamento — a **família inteira é revogada** e a API responde `401` ("Sessão comprometida"), obrigando novo login.
- **Logout:** revoga a família no servidor e limpa os dois cookies (`Max-Age=0`).
- **Expiração:** TTL absoluto por token (`trophix.refresh-token.expiration`, default `PT720H`) e, opcionalmente, idle timeout (`trophix.refresh-token.idle-timeout`, `PT0S` desabilita). Um job diário (03:30) purga tokens expirados com retenção de 7 dias para auditoria.
- **Rate limiting:** `/api/auth/refresh` e `/api/auth/logout` estão no grupo `auth` (anti força bruta).

> ⚠️ O cliente (SPA) deve **serializar** as chamadas de refresh (uma única em voo): duas renovações simultâneas do mesmo token disparam a detecção de reuso e encerram a sessão. O front-end ainda não consome `/api/auth/refresh` — pendência para adaptar o interceptor do `trophix-web`.

## Redefinição de senha (esqueci minha senha)

Fluxo anti-enumeração (a resposta de `forgot-password` é idêntica existindo ou não a conta):

```
POST /api/auth/forgot-password {email}
  │  (se a conta tiver e-mail + senha)
  ▼
gera token UUIDv7 (hash SHA-256 salvo) → e-mail com link via SMTP (Mailpit no dev)
  │  link: {TROPHIX_WEB_URL}/reset-password?token={token}
  ▼
POST /api/auth/reset-password {token, newPassword}
  ├─ valida: token existe, não consumido, não expirado (TTL 1h, uso único)
  ├─ re-hash da nova senha (BCrypt)
  ├─ marca token como consumido
  └─ revoga TODAS as sessões do usuário (famílias de refresh) — login obrigatório
```

- **Token** — UUIDv7 enviado no link; **apenas o hash SHA-256** é persistido (`password_reset_tokens`, migração V15). Nunca armazena o valor em claro.
- **E-mail** — template HTML `trophix-api/src/main/resources/templates/email/password-reset.html` no padrão visual do front (dark slate + violeta), com fallback de texto puro e envio **assíncrono**.
- **Config** — `trophix.password-reset.token-ttl` (default `PT1H`), `trophix.password-reset.frontend-url` (default `http://localhost:4200`), `trophix.mail.from` (default `no-reply@trophix.com`); SMTP via `spring.mail.*` (dev: `localhost:1025`, Mailpit).
- **Rate limiting** — `forgot-password` e `reset-password` estão no grupo `auth` (anti força bruta). Job diário (03:45) purga tokens expirados/consumidos.
- **Pendência front-end** — a rota `/reset-password?token=...` no `trophix-web` ainda não existe (criação a cargo do time de front).

## Armazenamento de objetos (MinIO / S3)

O `shared.infrastructure.storage.MinioStorageService` é o serviço base para imagens de guias e avatares: envia um `MultipartFile` para o bucket (default `trophix-media`) e devolve a **URL pública** do objeto. Na subida, o bucket é criado se não existir e configurado com policy `public-read` (links acessíveis direto, ou sirva por CDN/reverse proxy).

Configuração em `application.yml` (ou `.env`):

| Variável | Default | Descrição |
| -------- | ------- | --------- |
| `MINIO_URL` / `trophix.minio.url` | `http://localhost:9000` | Endpoint do MinIO |
| `MINIO_ACCESS_KEY` / `trophix.minio.access-key` | `minioadmin` | Access key (MINIO_ROOT_USER) |
| `MINIO_SECRET_KEY` / `trophix.minio.secret-key` | `minioadmin` | Secret key (MINIO_ROOT_PASSWORD) |
| `MINIO_BUCKET` / `trophix.minio.bucket` | `trophix-media` | Bucket usado pela plataforma |
| `MINIO_PUBLIC_URL` / `trophix.minio.public-url` | `http://localhost:9000` | Base da URL pública (CDN/proxy) |

No dev, o container `minio` do `docker-compose` já sobe com o console em `http://localhost:9001` (credenciais `minioadmin`/`minioadmin`). Falhas de armazenamento retornam `500` PT-BR (`StorageException`).

## Sincronização assíncrona (fila RabbitMQ)

Os syncs de perfil/jogos (on-demand e diário) e de troféus por jogo são **assíncronos via RabbitMQ**, desacoplando a entrada HTTP do processamento pesado:

```
Controller/Scheduler ──► RabbitMQ (trophix.sync.exchange ──► trophix.sync.queue) ──► @RabbitListener (worker Spring)
                                                                                        │
                                                                                        ▼
                                                    use cases de sync ──► sidecar ──► PSN ──► Postgres
```

- `POST /api/users/me/sync` e `POST /api/games/{gameId}/sync-trophies` respondem `202` e apenas **publicam um job** (`SyncJob`: `PROFILE_SYNC` ou `TROPHY_SYNC`).
- O worker (`shared/infrastructure/amqp/SyncJobConsumer`) consome a fila (prefetch 1, 2-4 consumers) e executa os mesmos use cases existentes.
- **Cooldown de 15 min validado no consumer** (autoritativo) — o scheduler publica livremente e jobs em cooldown são ignorados; o HTTP também valida para responder 429 ao usuário.
- **Retry + DLQ**: falhas transitórias do sidecar (`PsnServiceException`/circuito aberto) são reentregues até 3 vezes com backoff; erros permanentes (usuário/jogo inexistente, sem `accountId`) são logados e descartados; jobs esgotados caem na `trophix.sync.queue.dlq`.
- **Idempotência**: os upserts são seguros de reprocessar; `lastSyncedAt` é atualizado somente após o sync bem-sucedido.

## Variáveis de ambiente

### `.env` da raiz (docker-compose)

| Variável        | Obrigatória | Descrição |
| --------------- | ----------- | --------- |
| `POSTGRES_DB`     | não (default `trophix`) | Nome do banco |
| `POSTGRES_USER`   | não (default `trophix`) | Usuário do banco |
| `POSTGRES_PASSWORD` | não (default `trophix`) | Senha do banco |
| `NPSSO_TOKEN`     | **sim**     | Token PSN de 64 caracteres |
| `TROFEUS_CACHE_TTL_MS` | não (default `600000`) | Cache do sidecar p/ troféus (ms) |
| `RABBITMQ_USER` / `RABBITMQ_PASSWORD` | não (default `trophix`/`trophix`) | Credenciais do RabbitMQ |
| `MAIL_HOST` / `MAIL_PORT` | não (default `localhost`/`1025`) | SMTP (dev: Mailpit) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` / `MAIL_FROM` | não (default vazio / `no-reply@trophix.com`) | SMTP opcional p/ auth e remetente |
| `TROPHIX_WEB_URL` | não (default `http://localhost:4200`) | Base do link de redefinição de senha |
| `MINIO_ROOT_USER` / `MINIO_ROOT_PASSWORD` | não (default `minioadmin`/`minioadmin`) | Credenciais do servidor MinIO |
| `MINIO_URL` / `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` / `MINIO_BUCKET` / `MINIO_PUBLIC_URL` | não (defaults locais) | Config de acesso ao MinIO (ver seção de armazenamento) |

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
| POST | `/api/auth/login` | público | Login — emite access JWT + refresh token (cookies HttpOnly) e cria a família de sessão |
| POST | `/api/auth/refresh` | público | Rotaciona o refresh token (novo par, mesma família); reuso revoga a família (401) |
| POST | `/api/auth/logout` | autenticado | Revoga a família de refresh tokens no servidor e limpa os cookies |
| POST | `/api/auth/forgot-password` | público | Envia e-mail com link de redefinição (token UUIDv7, uso único, 1h) — resposta genérica p/ não vazar e-mails cadastrados |
| POST | `/api/auth/reset-password` | público | Redefine a senha (valida o token, revoga todas as sessões do usuário) |

### Usuários / vínculo PSN
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/users/link-request` | público | Gera token `TRFX-XXXX` (15 min) p/ pôr no About Me |
| POST | `/api/users/link-validate` | público | Valida o token no perfil e consome o ticket |
| POST | `/api/users/me/sync` | autenticado | Sincroniza nível/totais + jogos (202, assíncrono via fila) |
| GET | `/api/users/me/profile` | autenticado | Perfil do usuário logado |
| GET | `/api/users/me/games` | autenticado | Jogos do usuário logado (paginado) |
| GET | `/api/users/{username}/profile` | público | Perfil público (nível, progresso, totais) |
| GET | `/api/users/{username}/games` | público | Jogos públicos paginados (ordenados por última jogada) |
| GET | `/api/users/{userId}` | autenticado | Dados do usuário (inclui e-mail) |

### Games / Troféus
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| GET | `/api/public/games?page=&size=&search=` | público | **Catálogo** da base interna: jogos paginados ordenados por nº de donos (`user_games`); `search` filtra por nome (case-insensitive) |
| GET | `/api/public/games/trending?limit=10` | público | **Em alta**: jogos mais jogados/recém-sincronizados (limit default 10, máx 50) |
| GET | `/api/public/trophies/feed?page=&size=` | público | **Feed global de atividades**: quem conquistou qual troféu recentemente (`earnedAt` desc), com `username`, `avatar`, `trophyName`, `trophyIconUrl`, `gameName` |
| GET | `/api/users/me/games/{gameId}/missing-trophies` | autenticado | Troféus do jogo que o usuário **ainda não conquistou** |
| POST | `/api/games/{gameId}/sync-trophies` | autenticado | Sincroniza catálogo de troféus + conquistas (202, assíncrono via fila) |
| GET | `/api/games/{gameId}/detail` | autenticado | Detalhe do jogo p/ o usuário logado (progresso + contagem por raridade) |
| GET | `/api/games/{gameId}/my-trophies` | autenticado | Catálogo do jogo com status de conquista (earned/earnedAt) |
| GET | `/api/games/{gameId}/trophies` | público | Lista os troféus do jogo (com o UUID interno) |

### Guias
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/games/{gameId}/guides` | autenticado | Submete roadmap do jogo (`title`/`content` obrigatórios, PENDING) |
| POST | `/api/trophies/{trophyId}/guides` | autenticado | Submete dica de troféu (`title`/`content` obrigatórios, PENDING) |
| PATCH | `/api/guides/{guideId}/review?action=APPROVE|REJECT` | **ROLE_ADMIN** | Modera o guia |
| POST | `/api/guides/{guideId}/vote` | autenticado | Vota/desvota (toggle, 1 voto por usuário) |
| GET | `/api/trophies/{trophyId}/guides` | público | Dicas aprovadas daquele troféu (id, title, description, content, ...) |
| GET | `/api/games/np/{npCommunicationId}/guides` | público | Roadmaps aprovados daquele jogo (id, title, description, content, ...) |
| GET | `/api/games/{gameId}/authors/{authorId}/trophy-guides` | público | Dicas de troféus aprovadas do autor naquele jogo (1 query, JOIN Guide↔Trophy) |
| GET | `/api/guides?limit=20` | público | Últimos roadmaps aprovados (sem trophyId), ordenados por criação desc, com `gameName`/`imageUrl`/`authorName`/`currentUserVoted` |
| GET | `/api/guides/{guideId}` | público | Detalhe de um guia aprovado, com `gameName`/`imageUrl`/`authorName`/`currentUserVoted` |

### Admin (`/api/admin` — somente ROLE_ADMIN)
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| GET | `/api/admin/dashboard/stats` | **ROLE_ADMIN** | Métricas do dashboard: novos usuários, guias PENDING, sincronizações PSN (24h) e denúncias abertas, com tendências |
| GET | `/api/admin/sidecar/status` | **ROLE_ADMIN** | Health check do sidecar PSN: `200 {"status":"UP"}` online, `503 {"status":"DOWN"}` offline |
| GET | `/api/admin/system/health` | **ROLE_ADMIN** | Health check leve da própria API: `200 {"status":"UP"}` (sem consultas externas) |
| GET | `/api/admin/users?page=&size=&search=&role=` | **ROLE_ADMIN** | Lista paginada de usuários; `search` filtra por username/email (LIKE case-insensitive) e `role` por cargo — combináveis |
| PUT | `/api/admin/users/{userId}/roles` | **ROLE_ADMIN** | Substitui os cargos do usuário (`roles: ["ROLE_USER", ...]`); revoga as sessões dele |
| GET | `/api/admin/guides/pending?page=&size=` | **ROLE_ADMIN** | Fila de moderação: guias PENDING paginados com `authorName`/`gameName` |
| GET | `/api/admin/guides?page=&size=&status=&search=` | **ROLE_ADMIN** | Lista paginada de guias; `status` filtra exatamente por status (PENDING/APPROVED/REJECTED) e `search` busca case-insensitive no título ou nome do jogo — combináveis |
| PUT | `/api/admin/guides/{guideId}` | **ROLE_ADMIN** | Edita title/description/content/videoUrl do guia (status e metadados preservados) |
| DELETE | `/api/admin/guides/{guideId}` | **ROLE_ADMIN** | Exclui definitivamente o guia (e seus votos, via cascade) |
| POST | `/api/admin/guides/{guideId}/approve` | **ROLE_ADMIN** | Aprova o guia (PENDING → APPROVED) |
| POST | `/api/admin/guides/{guideId}/reject` | **ROLE_ADMIN** | Rejeita o guia (PENDING → REJECTED) |
| GET | `/api/admin/reports?page=&size=` | **ROLE_ADMIN** | Fila de denúncias abertas (status OPEN) |
| POST | `/api/admin/reports/{reportId}/resolve` | **ROLE_ADMIN** | Resolve a denúncia (OPEN → RESOLVED) |
| POST | `/api/admin/reports/{reportId}/dismiss` | **ROLE_ADMIN** | Descarta a denúncia (OPEN → DISMISSED) |
| GET | `/api/admin/settings` | **ROLE_ADMIN** | Configurações globais (ou defaults, se ainda não salvas) |
| PUT | `/api/admin/settings` | **ROLE_ADMIN** | Salva as configurações globais (siteName, contato, redes, hero, alerta, footer, palavras proibidas, meta) |

> Proteção central no `SecurityConfig` (`.requestMatchers("/api/admin/**").hasRole("ADMIN")`). A troca de cargos revoga os refresh tokens do usuário, forçando novo login com o token atualizado.

### Denúncias (Reports)
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| POST | `/api/reports` | autenticado | Registra denúncia: `{targetType: GUIDE\|USER\|COMMENT, targetId, reason}` (máx 500 chars) |

Denúncias ficam com status `OPEN` até o admin **resolver** ou **descartar** (`/api/admin/reports/...`). `targetId` é polimórfico (sem FK): para `GUIDE`/`USER` o alvo é validado no envio; `COMMENT` é reservado (sem feature ainda). O contador de denúncias abertas alimenta o `dashboard/stats`.

### Fórum (Forums)
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| GET | `/api/forums/categories` | público | Categorias com total de tópicos (por `orderIndex`) |
| GET | `/api/forums/categories/{categoryId}/topics?page=&size=` | público | Tópicos da categoria, ordenados por `updatedAt` desc |
| GET | `/api/forums/topics/{topicId}` | público | Detalhe do tópico + página de respostas (incrementa `viewsCount` assíncrono) |
| GET | `/api/forums/topics/{topicId}/replies?page=&size=` | público | Página de respostas do tópico (mais antigas primeiro) |
| POST | `/api/forums/topics` | autenticado | Cria tópico: `{categoryId, title, content}` |
| POST | `/api/forums/topics/{topicId}/replies` | autenticado | Responde um tópico (incrementa `repliesCount`/`updatedAt` do pai) |

Ao criar uma resposta, o módulo publica um **domain event local** (`ReplyCreatedEvent`) consumido via `@ApplicationModuleListener` (Spring Modulith) de forma **assíncrona e transacional**; o autor do tópico recebe um e-mail "Nova resposta no seu tópico" (via Mailpit no dev, link para `/forums/topics/{id}`). O journal de eventos usa as tabelas `event_publication`/`event_publication_archive` (migração V19/V20).

### Notícias (News)
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| GET | `/api/public/news?page=&size=` | público | Agregador RSS (PlayStation Blog, MeuPlayStation, IGN Brasil): página de notícias ordenadas por `publishedAt` desc, com `imageUrl` (fallback `og:image`) e `isFeatured` |

O worker (`news` → `NewsSyncScheduler`) busca os feeds a cada 30 min (delay inicial 20s), deduplica por `link`, publica `NewsDiscoveredEvent` via Modulith para cada notícia nova e marca a mais recente como destaque.

### Ofertas (Offers / LootBox)
| Método | Rota | Acesso | Descrição |
| ------ | ---- | ------ | --------- |
| GET | `/api/public/offers?page=&size=&category=` | público | Ofertas **ativas** paginadas; `category` filtra opcionalmente (ex.: Jogos, Consoles, Hardware, Colecionáveis) |
| POST | `/api/public/offers/{offerId}/track-click` | público | Contabiliza 1 clique no link de afiliado (incremento atômico `UPDATE ... clickCount = clickCount + 1`); 200 sem body |
| GET | `/api/admin/offers?page=&size=&category=` | **ROLE_ADMIN** | Todas as ofertas (inclusive inativas) |
| GET | `/api/admin/offers/analytics/top-clicked?limit=5` | **ROLE_ADMIN** | Ranking das ofertas mais clicadas (Top N por `clickCount` desc) |
| POST | `/api/admin/offers` | **ROLE_ADMIN** | Cria oferta: `{title, imageUrl, originalPrice, discountPrice, storeName, affiliateLink, category, isFlashDeal}` |
| PUT | `/api/admin/offers/{offerId}` | **ROLE_ADMIN** | Edita a oferta (recalcula `discountPercentage`; aceita `isActive`) |
| DELETE | `/api/admin/offers/{offerId}` | **ROLE_ADMIN** | Exclui a oferta |

O `discountPercentage` é sempre **derivado** dos preços no backend (`(original - discount) / original * 100`, arredondado half-up); o domínio rejeita `discountPrice > originalPrice`. Cada oferta carrega também o `clickCount` (métrica de cliques de afiliado, alimentada pelo `track-click` e exibida no ranking do dashboard).

> **Modulith:** o teste de arquitetura (`src/test/.../ApplicationModulesTest`) usa `ApplicationModules.of(...).verify()` **sem allowlist** — o projeto está 100% conforme (0 violações, ciclos quebrados, APIs expostas via `@NamedInterface`). Qualquer violação nova quebra o build.

## Fluxo do produto

1. **Vínculo da conta PSN** — `link-request` gera `TRFX-XXXX`; o jogador coloca no *About Me*; `link-validate` confere a propriedade.
2. **Cadastro** — email + senha + role `ROLE_USER`; avatar buscado na PSN.
3. **Sync do perfil** — `POST /api/users/me/sync` enfileira o sync (202) e o worker atualiza nível/totais e o histórico de jogos (`user_games`), com cooldown de 15 min validado no consumer e agendamento diário às 04:00.
4. **Sync de troféus por jogo** — `POST /api/games/{gameId}/sync-trophies` enfileira (202) e o worker persiste o catálogo (`trophies`) e as conquistas com data (`user_trophies`). O sidecar serve respostas com cache, e a página de detalhes dispara esse sync automaticamente a cada visita.
5. **Detalhes do jogo** — a página `/jogos/:id` mostra capa, plataforma, progresso e a contagem de Platina/Ouro/Prata/Bronze conquistadas (`GET /api/games/{gameId}/detail`), além da lista de troféus com `earned`/`earnedAt` (`GET /api/games/{gameId}/my-trophies`). O sync silencioso mantém os dados atualizados sem ação do usuário.
6. **Guias** — roadmaps por jogo e dicas por troféu, cada um com `title`/`description`/`content`, submetidos com status `PENDING`, moderados por admin, com votação anti-fraude (contador atômico + `UNIQUE (guide_id, user_id)`).
7. **Consulta pública** — dica de chefe via `GET /api/trophies/{trophyId}/guides`; roadmap de 40h via `GET /api/games/np/{npCommunicationId}/guides`; últimos roadmaps via `GET /api/guides`.

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
