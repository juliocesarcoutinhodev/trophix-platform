# Trophix Web

Este é o front-end oficial da plataforma **Trophix**, construído para consumir a API Spring Boot (Trophix API) e exibir guias de troféus da PSN, detalhes de jogos e perfis de usuários.

## Tecnologias Principais

- **[Angular 22](https://angular.dev/)** — Aplicação estruturada com componentes **Standalone** e foco total em reatividade usando **Signals**.
- **[Tailwind CSS v4](https://tailwindcss.com/)** — Estilização utilitária com suporte a tema dark e tipografia (`@tailwindcss/typography`) para renderização de Markdown.
- **[ngx-markdown](https://github.com/jfcere/ngx-markdown)** — Para renderizar o conteúdo gerado (guias e dicas em Markdown) diretamente nas páginas.
- **Vitest** — Runner de testes unitários.

## Pré-requisitos

- Node.js 20+
- Trophix API (Spring Boot) rodando localmente na porta `8080`
- Trophix PSN Sidecar rodando na porta `3000` (consumido indiretamente pela API)

## Instalação e Execução

1. Instale as dependências:
   ```bash
   npm install
   ```

2. Inicie o servidor de desenvolvimento:
   ```bash
   npm start
   ```

O servidor iniciará em `http://localhost:4200/`. 
> **Nota de Proxy:** O comando `npm start` (via `ng serve`) utiliza o arquivo `proxy.conf.json` para rotear automaticamente as chamadas da rota `/api/*` para `http://localhost:8080`, evitando problemas de CORS e garantindo que o cookie de autenticação `SameSite=Strict` funcione corretamente em desenvolvimento local.

## Estrutura do Projeto

O projeto segue um padrão de arquitetura focado em modularidade via diretórios (já que componentes são *standalone*):

```
src/
└── app/
    ├── core/          # Serviços core (Api, Auth), interceptors, guards, models e pipes globais.
    ├── layout/        # Componentes de estrutura da página (Navbar, Sidebar, Footer).
    └── pages/         # Componentes de páginas roteáveis (Dashboard, Login, Game Detail, etc).
```

## Autenticação (Cookies HttpOnly)

Diferente de aplicações SPA tradicionais, o **Trophix Web não gerencia e nem salva tokens no LocalStorage**.
Toda a autenticação é baseada nos cookies `trophix_jwt` e `trophix_refresh`, que são definidos pelo backend (`HttpOnly`, `SameSite=Strict`).

Isso significa que:
1. Ao realizar o login, a API seta os cookies automaticamente.
2. Nas requisições subsequentes para `/api`, o navegador anexa os cookies de forma transparente.
3. Não há necessidade de adicionar cabeçalhos `Authorization: Bearer ...` nos interceptors padrão de chamadas à API da nossa infraestrutura.

## Tarefas Pendentes / Roadmap do Front-end

Algumas features identificadas na documentação do backend que ainda requerem implementação no front-end:

- [ ] **Interceptor de Refresh Token:** O front-end precisa interceptar respostas `401 Unauthorized` (quando o JWT expirar), e realizar uma requisição sequencial e serializada (sem chamadas simultâneas) para `POST /api/auth/refresh`, antes de retentar a chamada original. Lembre-se que reuso do refresh revoga a família inteira da sessão.
- [ ] **Fluxo de Redefinição de Senha:** Criação da página `/reset-password?token=...` para permitir ao usuário informar a nova senha e finalizar o fluxo de recuperação.
- [ ] **Integração das Ofertas (Roadmap Futuro):** Preparar o front para consumir as ofertas que serão automatizadas via backend.

## Scripts Úteis

- `npm run build`: Compila o projeto para produção no diretório `dist/`. O build otimiza a aplicação com *Ahead-of-Time* (AOT) compiler.
- `npm run test`: Roda a suíte de testes unitários com Vitest.
