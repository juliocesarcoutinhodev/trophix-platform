# trophix-psn-sidecar

Microsserviço sidecar (Node.js + Express + [psn-api](https://www.npmjs.com/package/psn-api)) que faz a ponte entre a PSN e o backend Java Spring Boot do Trophix. Roda apenas localmente, sem exposição à internet.

## Como configurar o `.env`

1. Acesse `https://ca.account.sony.com/api/v1/ssocookie` logado na conta PSN que autorizará as consultas e copie o valor do cookie `npsso` (64 caracteres).
2. Crie o arquivo `.env` na raiz do projeto a partir do exemplo:

```bash
cp .env.example .env
```

3. Edite o `.env` e cole seu NPSSO:

```
NPSSO=seu_npsso_de_64_caracteres_aqui
PORT=3000
```

> O NPSSO também expira. Quando o refresh token estiver perto de vencer, o sidecar avisa no log e você precisará gerar um novo NPSSO.

## Como rodar

```bash
npm install
npm start        # ou npm run dev (auto-restart)
```

Na inicialização o serviço troca o NPSSO por access/refresh tokens e loga o sucesso da autenticação. Tokens expirados são renovados automaticamente via refresh token (proativo antes da chamada e reativo/retry se a PSN responder `Unauthorized`).

## Endpoints

### `GET /api/perfil/:psnId`

Retorna o perfil público do usuário:

```json
{
  "psnId": "julio",
  "aboutMe": "Sou um gamer",
  "avatarUrl": "https://..."
}
```

`404` quando o perfil é privado ou não existe.

### `GET /api/jogos/:npCommunicationId/trofeus`

Retorna a lista base de troféus do jogo (grupo `all`, inclui DLCs):

```json
[
  {
    "idTrofeu": 1,
    "nome": "Primeiro Troféu",
    "descricao": "Descrição do troféu",
    "tipo": "Gold",
    "iconeUrl": "https://..."
  }
]
```

`tipo` assume `Bronze`, `Silver`, `Gold` ou `Platinum`. `404` quando o jogo não existe na PSN.

### `GET /api/jogos/:npCommunicationId/details`

Retorna os dados oficiais do título (nome, capa, plataforma e total de troféus) a partir do `npCommunicationId` (formato NPWR). Usado pelo painel admin da Trophix API para a importação de jogos:

```json
{
  "name": "ASTRO BOT",
  "coverUrl": "https://psnobj.prod.dl.playstation.net/psnobj/NPWR24170_00/...png",
  "platform": "PS5",
  "totalTrophies": 63
}
```

- `400` quando o id não é um NPWR (ex.: PPSA/CUSA, que não possuem dados de troféus).
- `404` quando o jogo não existe na PSN.
- `502` quando a consulta à PSN falha.

## Estrutura

```
src/
├── index.js          # bootstrap: autentica e sobe o Express
├── config.js         # leitura do .env
├── psnClient.js      # autenticação NPSSO + refresh de token + wrapper com retry
└── routes/
    ├── perfil.js     # GET /api/perfil/:psnId
    ├── trofeus.js    # GET /api/jogos/:npCommunicationId/trofeus e .../trofeus-conquistados/:accountId
    └── detalhes.js   # GET /api/jogos/:npCommunicationId/details
```