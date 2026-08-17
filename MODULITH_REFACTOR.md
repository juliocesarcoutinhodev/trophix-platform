# Refatoração para 100% de conformidade com o Spring Modulith

Checklist de trabalho. Objetivo: fazer o `ApplicationModulesTest` passar **sem** allowlist,
mantendo o app funcionando a cada fase.

Estado atual: **629 violações** (107 únicas) — todas acessos entre módulos a subpacotes
internos + 3 ciclos (`shared`, `auth`, `games`). A allowlist no `ApplicationModulesTest`
segura o build até concluirmos.

Progresso:
- **[Fase 1 concluída]** — `shared` agora é módulo folha (zero imports de outros módulos),
  0 ciclos detectados. `Role` + persistência de roles movidos para `shared`; segurança
  (`SecurityConfig`, `JwtAuthenticationFilter`, `CustomUserDetailsService`, `AuthenticatedUser`)
  movida para `auth`; orquestração de sync movida para o novo módulo `sync`.
- **Decisão de design (Fase 1.3):** a porta `SyncJobPublisher` **permanece em `shared`**
  (apenas interface, folha). Se ela fosse para o módulo `sync`, `users`/`trophies`
  dependeriam de `sync` e `sync` depende deles → ciclo. Mantendo a porta em `shared`:
  `users→shared`, `trophies→shared`, `sync→{shared,users,trophies}`. O impl RabbitMQ,
  o consumer e a config ficam em `sync`.

---

## Fase 0 — Decisões e fundação

- [ ] **Grafo-alvo de dependências** (direção única, sem ciclos):

  ```
  shared (folha)
    └─► auth ──► users ──► games ──► trophies ──► guides ──► reports / forums ──► admin
  ```
  Todo acesso entre módulos deve passar pela **API exposta** do módulo de destino.

- [ ] **Estratégia de exposição de API**: `@NamedInterface` nos subpacotes
  (recomendado — evita mover arquivos). Padrão:

  ```java
  // com/trophix/api/users/application/ports/out/package-info.java
  @NamedInterface("out")
  package com.trophix.api.users.application.ports.out;
  ```

  Alternativa (só se necessário): mover os tipos para o pacote raiz do módulo.

- [ ] Gerar diagramas com `Documenter` (`spring-modulith-starter-docs` em scope test)
  para acompanhar a evolução da estrutura.

---

## Fase 1 — Tornar `shared` um módulo folha (quebra os ciclos `shared`, `auth`, `games`)

- [x] **1.1 Mover `Role` de `auth` → `shared`** (quebra o ciclo auth↔users; auth e users
      passam a depender só de shared):
  - `auth/model/Role.java`
  - `auth/infrastructure/adapter/out/RoleJpaEntity.java`
  - `auth/infrastructure/adapter/out/RoleSpringDataRepository.java`
  - `auth/infrastructure/adapter/out/PostgresRoleAdapter.java`
  - `auth/infrastructure/adapter/out/RoleDataInitializer.java`
  - `auth/application/ports/out/RoleRepositoryPort.java`
- [x] **1.2 Mover a segurança de `shared.infrastructure.security` → `auth`** (remove
      shared→auth via `TokenValidatorPort`/`Role`):
  - `SecurityConfig.java`
  - `JwtAuthenticationFilter.java`
  - `CustomUserDetailsService.java`
  - `AuthenticatedUser.java` (passa a ser exposto pela API do auth)
- [x] **1.3 Mover a orquestração de sync do `shared` → novo módulo `sync`** (remove
      shared→users/trophies):
  - `shared/infrastructure/amqp/SyncJobConsumer.java`
  - `shared/infrastructure/amqp/RabbitSyncJobPublisher.java`
  - `shared/infrastructure/amqp/SyncQueueConfig.java`
  - `shared/application/ports/out/SyncJobPublisher.java`
  - `shared/domain/SyncJob.java`
  - O módulo `sync` depende de `users` + `trophies` (via APIs expostas).
  - Nota: a porta `SyncJobPublisher` e o `SyncJob` ficam em `shared` (folha) para evitar
    ciclo `sync`↔`users`/`trophies`; a orquestração/impl/queue ficam em `sync`.
- [x] **1.4 Conferir que `shared` só tenha** (zero imports de outros módulos):
  - `UuidV7`, exceptions PT-BR, `UuidV7Id`, storage (MinIO),
    `RestClient`/`SidecarClient`, rate-limit, circuit-breaker, `AsyncConfig`,
    `Role` (+ port/adapters/JPA).

---

## Fase 2 — Expor a API pública de cada módulo (`@NamedInterface`)

- [ ] `shared`: exceptions, `UuidV7`, `UuidV7Id`, `SidecarClient`, storage
- [ ] `auth`: `AuthenticatedUser`, `EmailSenderPort`, `RefreshTokenRepository`, `TokenValidatorPort`
- [ ] `users`: `UserRepository`, `User`, `PsnProfileFetcherPort`, `PsnProfile`
- [ ] `games`: `GameRepositoryPort`, `Game`, `UserGameRepositoryPort`, `UserGame(Summary)`
- [ ] `trophies`: `TrophyRepositoryPort`, `Trophy`, `UserTrophyRepositoryPort`
- [ ] `guides`: `GuideRepositoryPort`, `Guide`, `GuideStatus`, `GuideListItem`,
      `GuideResponse`, `GuideEnricher`, casos de uso de moderação (usados pelo admin)
- [ ] `reports`: `ReportRepository`, `Report`, `ReportStatus`
- [ ] **Unificar `MessageResponse`** (duplicado hoje em `auth` e `guides`) em `shared`.

---

## Fase 3 — Quebrar o acoplamento de infraestrutura JPA (colunas UUID planas)

Padrão já usado em `forums`/`reports`: FKs como colunas UUID, sem relação JPA entre módulos.

- [ ] `games/UserGameEntity.user` (`@ManyToOne UserJpaEntity`) → coluna `user_id UUID`
- [ ] `trophies/TrophyEntity.game` (`@ManyToOne GameEntity`) → coluna `game_id UUID`
- [ ] `guides/GuideEntity` (game/trophy/author `@ManyToOne`) → colunas
      `game_id` / `trophy_id` / `author_id UUID`
- [ ] Remover dos adapters a injeção de repos de outros módulos
      (`UserSpringDataRepository`, `GameSpringDataRepository`, `TrophySpringDataRepository` — usados via `getReferenceById`)
- [ ] Manter `users.roles` (vira relação com `shared.Role` — ok)

---

## Fase 4 — Desacoplar o `admin` (maior consumidor)

- [ ] `admin` usa `guides...dto.GuideResponse` / `MessageResponse` / `GuideWebMapper` /
      `GuideEnricher` → passar a usar apenas a API exposta do guides
- [ ] Criar `GuidesAdminApi` no módulo guides (listar todas com filtros status/search/tipo,
      revisar, editar, excluir) e o `AdminController` apenas delega
- [ ] Redirecionar `admin` → `auth.RefreshTokenRepository` / `Role` / `RoleRepositoryPort`,
      `users.UserRepository`, `reports.ReportRepository`, `shared.SidecarClient` pelas named interfaces

---

## Fase 5 — Ciclos remanescentes

- [ ] Rodar `detectViolations()` e resolver resíduos (ex.: enums/read models em cadeia
      guides→games→trophies)
- [ ] Re-aplicar queries JPQL/FKs que dependiam das relações de entidades agora planas

---

## Fase 6 — Verificação e regressão

- [ ] Remover entradas da **allowlist** do `ApplicationModulesTest` conforme cada item avança
- [ ] Rodar `mvn test` (verificação do Modulith) + `mvn package` após cada fase
- [ ] Adicionar `@ApplicationModuleTest` por módulo (testes isolados de contrato)
- [ ] Gerar os diagramas do `Documenter` e mantê-los no build (AGENTS.md)
- [ ] **Smoke test completo** após cada fase: auth/login, sync PSN, guias, fórum
      (criar tópico + resposta + e-mail via `@ApplicationModuleListener`), dashboard admin

---

## Ordem sugerida

**Fase 1 → Fase 3 → Fase 2 → Fase 4 → Fase 5 → Fase 6**

- A Fase 2 pode rodar em conjunto com a 1 (named interfaces à medida que os pacotes saem do `shared`).
- A Fase 4 fica para o fim (depende de tudo).
- Cada fase deve terminar com `mvn test` verde (allowlist decrescente) e o app subindo.
