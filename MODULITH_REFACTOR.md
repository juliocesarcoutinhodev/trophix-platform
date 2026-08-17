# Refatoração para 100% de conformidade com o Spring Modulith

Checklist de trabalho. Objetivo: fazer o `ApplicationModulesTest` passar **sem** allowlist,
mantendo o app funcionando a cada fase.

Estado atual: **629 violações** (107 únicas) — todas acessos entre módulos a subpacotes
internos + 3 ciclos (`shared`, `auth`, `games`). A allowlist no `ApplicationModulesTest`
segura o build até concluirmos.

Situação após Fases 1–5: **0 violações**. O `ApplicationModulesTest` agora usa
`ApplicationModules.of(...).verify()` sem allowlist. Falta apenas a Fase 6
(testes por módulo, diagramas e smoke test final).

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
- **[Fase 3 concluída]** — acoplamento de infraestrutura JPA entre módulos eliminado
  (colunas UUID planas, sem `@ManyToOne`/`getReferenceById` cross-módulo):
  - `games/UserGameEntity.user` → coluna `user_id UUID`; porta `findByUsername`
    removida (o use case de `users` resolve username→userId via `UserRepository`)
  - `trophies/TrophyEntity.game` → `game_id UUID`; `trophies/UserTrophyEntity.user`
    → `user_id UUID`
  - `guides/GuideEntity` (trophy/game/author) → `trophy_id`/`game_id`/`author_id UUID`;
    `guides/GuideVoteEntity.user` → `user_id UUID`
  - Removida injeção de `UserSpringDataRepository`/`GameSpringDataRepository`/
    `TrophySpringDataRepository` de outros módulos nos adapters
  - Queries que buscavam por nome do jogo (`findAllFiltered`, `findLatestRoadmaps`) e
    por `psnTrophyId` (`findTrophyTipsByAuthorAndGame`) reescritas como **native SQL**
    (join pelas colunas planas), preservando o comportamento sem acoplamento Java
  - **Ciclos restantes:** apenas `games→trophies→games` e `games→trophies→users→games`
    (nível de use case/porta, não JPA) — tratados na Fase 5.
- **[Fase 2 concluída]** — APIs públicas de cada módulo expostas via `@NamedInterface`
  (package-info). Todas as violações de "non-exposed type" foram eliminadas:
  **629 → 2 violações** (as 2 são o mesmo ciclo `games`). A allowlist foi reduzida a
  apenas a entrada do ciclo.
  - `shared`: exception, domain, model, ports.out, persistence, ratelimit, web, jpa (roles)
  - `auth`: ports.out (email/tokens/refresh), infrastructure.security (`AuthenticatedUser`)
  - `users`: ports.out (UserRepository/PsnProfileFetcherPort), model, async (UserProfileSyncExecutor)
  - `games`/`trophies`/`reports`: ports.out + model (+ `trophies.ports.in` p/ `SyncGameTrophiesUseCase`)
  - `guides`: ports.in/out, service (GuideEnricher), model, dto, mapper
  - **`MessageResponse` unificado** em `shared.dto` (removidas as duplicatas de `auth` e `guides`).

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

- [x] `shared`: exceptions, `UuidV7`, `UuidV7Id`, `SidecarClient`, storage, `Role`/ports
- [x] `auth`: `AuthenticatedUser`, `EmailSenderPort`, `RefreshTokenRepository`, `TokenValidatorPort`
- [x] `users`: `UserRepository`, `User`, `PsnProfileFetcherPort`, `PsnProfile`, `UserProfileSyncExecutor`
- [x] `games`: `GameRepositoryPort`, `Game`, `UserGameRepositoryPort`, `UserGame(Summary)`
- [x] `trophies`: `TrophyRepositoryPort`, `Trophy`, `UserTrophyRepositoryPort`, `SyncGameTrophiesUseCase`
- [x] `guides`: `GuideRepositoryPort`, `Guide`, `GuideStatus`, `GuideListItem`,
      `GuideResponse`, `GuideEnricher`, casos de uso de moderação (usados pelo admin)
- [x] `reports`: `ReportRepository`, `Report`, `ReportStatus`
- [x] **Unificar `MessageResponse`** (duplicado hoje em `auth` e `guides`) em `shared`.
- [x] Allowlist reduzida: **629 → 2** (apenas o ciclo `games` restante).

---

## Fase 3 — Quebrar o acoplamento de infraestrutura JPA (colunas UUID planas)

Padrão já usado em `forums`/`reports`: FKs como colunas UUID, sem relação JPA entre módulos.

- [x] `games/UserGameEntity.user` (`@ManyToOne UserJpaEntity`) → coluna `user_id UUID`
- [x] `trophies/TrophyEntity.game` (`@ManyToOne GameEntity`) → coluna `game_id UUID`
- [x] `trophies/UserTrophyEntity.user` (`@ManyToOne UserJpaEntity`) → coluna `user_id UUID`
- [x] `guides/GuideEntity` (game/trophy/author `@ManyToOne`) → colunas
      `game_id` / `trophy_id` / `author_id UUID`
- [x] `guides/GuideVoteEntity.user` (`@ManyToOne UserJpaEntity`) → coluna `user_id UUID`
- [x] Remover dos adapters a injeção de repos de outros módulos
      (`UserSpringDataRepository`, `GameSpringDataRepository`, `TrophySpringDataRepository` — usados via `getReferenceById`)
- [x] Reescrever queries com join cross-módulo como native SQL (nome do jogo,
      `psnTrophyId`) ou via portas (username→userId em `GetUserGamesUseCaseImpl`)
- [x] Manter `users.roles` (vira relação com `shared.Role` — ok)

---

## Fase 4 — Desacoplar o `admin` (maior consumidor)

- [x] Com a Fase 2 (APIs expostas via `@NamedInterface`), `admin` já acessa apenas a API
      pública dos módulos guides/reports/auth/users/shared — nada mais a fazer aqui.

---

## Fase 5 — Ciclos remanescentes

- [x] Quebrado o ciclo `games→trophies→users→games`:
  - `GetGameDetailUseCaseImpl` movido de `games` para `trophies` (que já depende de
    `games`); `games.application.ports.in` exposto via `@NamedInterface` para que o
    `trophies` implemente a porta de entrada sem criar ciclo.
  - Corrigido o binding de `GuideStatus` nas queries nativas do guides (ligado como
    `String` via `status.name()`), eliminando o erro "character varying = smallint".
- [x] `detectViolations()` → **0 violações**; allowlist removida por completo.

---

## Fase 6 — Verificação e regressão

- [x] Remover entradas da **allowlist** do `ApplicationModulesTest` conforme cada item avança
      (removida por completo — teste agora usa `verify()` estrito)
- [x] Rodar `mvn test` (verificação do Modulith) + `mvn package` após cada fase
- [ ] Adicionar `@ApplicationModuleTest` por módulo (testes isolados de contrato)
- [ ] Gerar os diagramas do `Documenter` e mantê-los no build (AGENTS.md)
- [ ] **Smoke test completo** após cada fase: auth/login, sync PSN, guias, fórum
      (criar tópico + resposta + e-mail via `@ApplicationModuleListener`), dashboard admin
      (parcialmente validado nas fases 3 e 5)

---

## Ordem sugerida

**Fase 1 → Fase 3 → Fase 2 → Fase 4 → Fase 5 → Fase 6**

- A Fase 2 pode rodar em conjunto com a 1 (named interfaces à medida que os pacotes saem do `shared`).
- A Fase 4 fica para o fim (depende de tudo).
- Cada fase deve terminar com `mvn test` verde (allowlist decrescente) e o app subindo.
