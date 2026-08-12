# Trophix Platform

Monorepo do ecossistema Trophix: uma plataforma de guias de troféus da PSN.

```
trophix-platform/
├── docker-compose.yml          # orquestra banco + sidecar
├── .env.example                # documentacao das variaveis (ficcao)
├── .env                        # variaveis reais locais (NAO versionar)
├── trophix-api/                # backend Java / Spring Boot 4.0.7 (em construcao)
└── trophix-psn-sidecar/        # microsservico Node.js que consome a PSN
```

## Pré-requisitos

- Docker + Docker Compose
- Node.js 20+ (apenas se for rodar o sidecar fora do container)
- JDK 25 + Maven (apenas para desenvolvimento do trophix-api)

## Subir tudo (recomendado)

Docker Compose lê automaticamente o arquivo `.env` da raiz. Crie-o a partir do exemplo:

```bash
cp .env.example .env
# Edite o .env e cole seu NPSSO_TOKEN real (64 caracteres)
```

Depois, um único comando:

```bash
docker compose up -d --build
```

Serviços resultantes:

| Serviço       | Container           | Porta          | Descrição                                  |
| ------------- | ------------------- | -------------- | ------------------------------------------ |
| `postgres`    | `trophix-postgres`  | `localhost:5432` | PostgreSQL 18, volume persistente `postgres-data` |
| `psn-sidecar` | `trophix-psn-sidecar` | `localhost:3000` | Sidecar Node.js que autentica e consulta a PSN |

O sidecar autentica na PSN na inicialização; acompanhe o log para confirmar:

```bash
docker compose logs -f psn-sidecar
```

## Variáveis de ambiente (`.env` da raiz)

| Variável          | Obrigatória | Descrição                                                      |
| ----------------- | ----------- | -------------------------------------------------------------- |
| `POSTGRES_DB`     | não (default `trophix`) | Nome do banco PostgreSQL                               |
| `POSTGRES_USER`   | não (default `trophix`) | Usuário do banco PostgreSQL                             |
| `POSTGRES_PASSWORD` | não (default `trophix`) | Senha do banco PostgreSQL                            |
| `NPSSO_TOKEN`     | **sim**     | Token PSN de 64 caracteres (o compose falha se ausente)        |

> Obtenha o NPSSO em `https://ca.account.sony.com/api/v1/ssocookie` logado na PSN. Ele vale como senha e expira (~60 dias). O sidecar usa-o apenas no boot: a partir daí renova access/refresh tokens sozinho.

## Endpoints do sidecar

| Rota                                  | Descrição                                          |
| ------------------------------------- | -------------------------------------------------- |
| `GET /api/perfil/:psnId`              | Perfil público (psnId, aboutMe, avatarUrl)         |
| `GET /api/jogos/:npCommunicationId/trofeus` | Lista de troféus de um jogo                  |
| `GET /api/resumo/:psnId`              | Nível, progresso e contagem de troféus do jogador  |
| `GET /api/jogos-usuario/:accountId`   | Histórico de jogos com progresso e troféus         |

`404` para perfis privados/inexistentes; `502` com `detalhe` quando a PSN falha.
Collection Postman: `trophix-psn-sidecar/trophix-psn-sidecar.postman_collection.json`.

## Rodar serviços isoladamente (desenvolvimento)

### Sidecar fora do Docker

```bash
cd trophix-psn-sidecar
cp .env.example .env   # NPSSO_TOKEN
npm install
npm start              # porta 3000
```

### API Java

```bash
cd trophix-api
mvn spring-boot:run    # dev profile, lê o .env da própria pasta
```

## Segurança

- `.env` e `.env.*` estão no `.gitignore` — o `.env.example` é o único versionado.
- O `NPSSO_TOKEN` é equivalente à senha da conta PSN. Não o exponha em logs, issues ou commits.
- `docker compose config` lista as variáveis resolvidas — use para conferir antes de subir.