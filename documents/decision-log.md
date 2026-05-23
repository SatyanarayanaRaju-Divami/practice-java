# Decision Log — Family League

All architectural, design, and implementation decisions are recorded here with justification.
Any future deviation from these decisions must be added as a new entry.

---

## DL-001 — Java 17 + Spring Boot 3.x

**Decision:** Use Java 17 (LTS) with Spring Boot 3.x.

**Justification:**
- Java 17 is the current LTS release — stable, widely supported, and required by Spring Boot 3.x.
- Spring Boot 3.x brings Jakarta EE 10, improved native image support, and latest Spring Security 6.
- Aligns with the requirement for "Standards of Java, JEE, Spring and in general backend development good practices."

---

## DL-002 — PostgreSQL over MySQL

**Decision:** Use PostgreSQL as the primary datastore.

**Justification:**
- Requirements explicitly list PostgreSQL as the preferred choice.
- PostgreSQL has superior support for UUID generation (`gen_random_uuid()`), JSON columns, and advanced constraint types (which are needed for trigger-based lock enforcement).
- MySQL was considered but PostgreSQL was retained as the default — if MySQL is chosen in future, the migration must be re-evaluated for UUID and trigger compatibility.

---

## DL-003 — Flyway for Database Migrations

**Decision:** Use Flyway for all schema management. `spring.jpa.hibernate.ddl-auto` is set to `validate`.

**Justification:**
- Requirements list Flyway as the recommended migration tool.
- `ddl-auto=validate` ensures Hibernate validates the schema on startup but never modifies it — all DDL is owned by Flyway versioned scripts.
- This prevents silent schema drift between environments and makes all changes reviewable via version control.
- `ddl-auto=update` was used during initial scaffolding only and was removed once Flyway was introduced.

---

## DL-004 — UUID as Primary Key for All Entities

**Decision:** All domain entity primary keys use UUID (`gen_random_uuid()` in PostgreSQL, `@GeneratedValue(strategy = GenerationType.UUID)` in JPA).

**Justification:**
- UUIDs are safe to expose in APIs without leaking record counts or sequence information.
- Enables ID generation at the application layer before a DB round-trip.
- The initial scaffold used `BIGSERIAL` (Long) for the `users` table — this was dropped and recreated with UUID once the full data model was established.
- `app_config` is the only exception — it uses a `VARCHAR` natural key because its primary key is a human-readable config key string.

---

## DL-005 — Soft Delete Strategy

**Decision:** No domain records are permanently deleted. All deletes set `is_deleted = true`, `deleted_at`, and `deleted_by` on the record.

**Justification:**
- Requirements explicitly state: "No records get deleted permanently, it is only a soft delete unless needed. Need must be logged as a decision in this log."
- Soft deletes preserve audit trail and allow recovery of accidentally deleted data.
- JPA `@SQLRestriction("is_deleted = false")` (Spring Boot 3.x / Hibernate 6 equivalent of `@Where`) is applied to all entities so deleted records are automatically excluded from all queries.
- Any hard delete requirement must be added as a new entry in this log.

---

## DL-006 — BaseEntity for Audit Fields

**Decision:** All domain entities extend an abstract `BaseEntity` class that carries `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`, and `is_deleted`.

**Justification:**
- Requirements state: "All the data changes must be captured as standard audit data capture."
- Centralising audit fields in `BaseEntity` eliminates repetition and ensures consistency.
- Spring Data JPA Auditing (`@EnableJpaAuditing`, `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`) auto-populates timestamp fields.
- `created_by` and `updated_by` are wired via `AuditorAware<UUID>` — currently returns `Optional.empty()` as a placeholder until JWT authentication is implemented.
- `app_config` does not extend `BaseEntity` — it is a configuration store, not a domain entity with an auditable lifecycle (see DL-016).

---

## DL-007 — League as Umbrella, Season as Instance

**Decision:** A `League` is a named umbrella entity. A `Season` is a runnable instance of a league. Teams are independent of seasons and reusable across them.

**Justification:**
- Requirements explicitly state: "Teams are independent of League season (Each team could play many leagues of Same name). This forces a definition of League as an umbrella and season as an instance of league."
- This avoids duplicating team records across seasons and correctly models real-world sports leagues where the same franchise participates year after year.

---

## DL-008 — Two Separate Leaderboard Concepts

**Decision:** Two distinct tables serve different leaderboard purposes:
- `league_standings` — tracks actual real-world team positions (wins, draws, losses, league points) per season.
- `leaderboard` — tracks user prediction points and user rankings within a season.

**Justification:**
- The requirements reference "League level leaderboard of team positions" (real-world standings) separately from user prediction ranking.
- Conflating these would make the data model ambiguous and scoring logic incorrect.
- `league_standings` is updated after every match result. `leaderboard` is updated after the async scoring engine runs.
- Final league prediction scoring (did the user correctly predict team positions 1-to-n?) compares `predictions_league` against the final `league_standings` at season close.

---

## DL-009 — Scoring Rule Interpretation

**Decision:** One correct prediction earns one point. Submitting a prediction that turns out to be wrong earns zero points.

**Justification:**
- Requirements state: "One Prediction add one point to the user score in the league."
- This wording is ambiguous — it could mean submitting any prediction gives a point regardless of outcome.
- Interpreted as "correct prediction = 1 point" because awarding points for incorrect predictions removes all competitive value from the platform.
- For ties: requirements explicitly state "Ties count as official results and either side gets 1 point" — so both users who predicted either team win 1 point for the match winner prediction.
- Points are **never accepted via API input** — the scoring engine is the only writer to the `scores` table.

---

## DL-010 — DB-Level Prediction Lock via Trigger (Not CHECK Constraint)

**Decision:** Prediction lock enforcement at the database level is implemented as a `BEFORE INSERT OR UPDATE` PostgreSQL trigger, not a `CHECK` constraint.

**Justification:**
- Requirements state: "Prediction lock must be enforced at the database level."
- PostgreSQL `CHECK` constraints cannot reference other tables — enforcing `submitted_at <= matches.lock_time` via CHECK is not possible since `lock_time` lives in a different table.
- A `BEFORE INSERT OR UPDATE` trigger on `predictions_match` (and a corresponding one on `predictions_league`) queries the related match/season lock time and raises an exception if the window has closed.
- This guarantees integrity even if application-layer logic is bypassed.
- Triggers are added via `V11__add_prediction_lock_triggers.sql` Flyway migration.

---

## DL-011 — Async Scoring Engine

**Decision:** Leaderboard and score recalculation runs asynchronously after an admin publishes a result, using Spring `@Async`.

**Justification:**
- Requirements state: "Leader board calculations needs to be performed in async mode and informed to admin via email."
- Running scoring synchronously would block the admin's publish-result API call for potentially seconds (large number of users × predictions).
- The publish API returns immediately after saving the result. The async job handles scoring, leaderboard update, admin email, and user score notification emails.
- If the async job fails, the error is logged and admin is notified separately.

---

## DL-012 — Player Lives in Its Own `player/` Package

**Decision:** The `Player` entity, repository, service, and controller live in the `player/` package — separate from `team/`.

**Justification:**
- Players change their team affiliation across seasons (e.g., a player who played for Team A in Season 1 may move to Team B in Season 2). Nesting Player inside `team/` would model them as permanently owned by one team, which does not reflect this reality.
- Player is now an independent entity with its own CRUD lifecycle: `POST /api/v1/players` (with `teamId` in the body), `PUT /api/v1/players/{id}` (with optional `teamId` for transfer), etc.
- `player_of_match` (POTM) in match results and `predicted_potm_player_id` in match predictions reference Player directly — these cross-cutting usages make more sense when Player is a peer-level entity rather than a child of Team.
- `Player` still carries a non-nullable `team_id` FK — a player always belongs to a team at any given time, but the team can be updated via a transfer.
- A convenience endpoint `GET /api/v1/players/by-team/{teamId}` is provided for listing players within a specific team.
- This supersedes the earlier decision to co-locate Player under `team/`. The original decision was based on the assumption that players belonged permanently to one team.

---

## DL-013 — Season in `league/` Package

**Decision:** The `Season` entity lives in the `league/` package.

**Justification:**
- A season cannot exist without a league (`league_id` is non-nullable).
- Seasons are created and listed through league-scoped endpoints (`POST /leagues/{leagueId}/seasons`).
- Same parent-child co-location principle as DL-012.

---

## DL-014 — `MatchResult.published_by` as Raw UUID (Not FK)

**Decision:** `match_results.published_by` is stored as a plain `UUID` column without a foreign key constraint to `users`.

**Justification:**
- Adding a FK to `users` here creates no meaningful navigation benefit — this field is purely an audit field recording which admin published the result.
- Audit fields (`created_by`, `updated_by`, `deleted_by`, `published_by`) are stored as raw UUIDs across the entire schema for the same reason — they are reference data, not navigation relationships.
- This avoids cascading delete issues if a user is soft-deleted after having published results.

---

## DL-015 — First Admin Bootstrapped via Migration

**Decision:** The first admin account is created via a Flyway seed migration. Admin creation via API requires an existing authenticated admin.

**Justification:**
- There is no public "register as admin" endpoint — this would be a security vulnerability.
- The API for admin creation (`POST /auth/admin`) is protected by `ROLE_ADMIN`.
- A bootstrapped admin account (seeded in a future `V12__seed_admin.sql`) is the only entry point for the first operator.
- Credentials for the seeded admin must not be hardcoded — they should be injected via environment variables at migration time.

---

## DL-016 — `app_config` Has No `BaseEntity`

**Decision:** The `app_config` table uses a `VARCHAR` primary key and does not extend `BaseEntity`. It has no audit or soft-delete fields.

**Justification:**
- `app_config` is a key-value configuration store, not a domain entity with a full lifecycle.
- Config entries are updated in-place — there is no concept of "deleting" a config key.
- Audit for config changes is handled at the service layer (logging who changed what and when) rather than DB-level audit columns.
- Adding `BaseEntity` inheritance would require a UUID PK, breaking the natural-key design (`key` as PK is intentional for direct lookup).

---

## DL-017 — API Base Path `/api/v1`

**Decision:** All API endpoints are prefixed with `/api/v1`.

**Justification:**
- Versioning the API from the start allows breaking changes in a future `/api/v2` without disrupting existing clients.
- `/api` prefix separates application endpoints from static resources and actuator paths.

---

## DL-018 — Consistent Response Envelope

**Decision:** All API responses are wrapped in a standard envelope:
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "..."
}
```

**Justification:**
- Requirements state: "Consistent API agreements (Request and Response Data Structures)."
- A standard envelope makes it easy for any client to handle success and error states uniformly without inspecting HTTP status codes alone.
- Error responses include `code`, `message`, and `fieldErrors` for validation failures.

---

## DL-019 — Constructor Injection Over Field Injection

**Decision:** All Spring beans use constructor injection. `@Autowired` on fields is not used.

**Justification:**
- Constructor injection makes dependencies explicit and enables easier unit testing (dependencies can be passed directly without a Spring context).
- Field injection with `@Autowired` hides dependencies and makes classes harder to test in isolation.
- Aligns with Spring's own recommendation and general JEE best practices required by the submission checklist.

---

## DL-020 — `@Transactional` at Service Layer Only

**Decision:** `@Transactional` annotations are placed on service classes and methods only — never on controllers or repositories.

**Justification:**
- Controllers are responsible for HTTP concerns only — transactions are a business/data concern.
- Spring Data repositories already wrap individual operations in transactions by default.
- Placing `@Transactional` at the service layer ensures a single transaction spans all repository calls within a service method, preventing partial writes.
- Read-only queries use `@Transactional(readOnly = true)` to enable query optimisations in Hibernate.

---

## DL-021 — Build Tool: Maven

**Decision:** Maven (`pom.xml`) is used as the build tool.

**Justification:**
- The project was initialised via Spring Initializr with Maven.
- Both Maven and Gradle are acceptable per requirements — Maven was chosen for its explicit, declarative dependency management which is easier for learners to follow.
- The Maven Wrapper (`./mvnw`) is committed so the project builds from a clean clone without a Maven installation.

---

## DL-022 — No Lookup Tables for Enumerated Values

**Decision:** Enumerated domain values (`UserRole`, `SeasonStatus`, `MatchStatus`, `EmailStatus`, `PredictionType`) are implemented as Java enums with PostgreSQL `CHECK` constraints — not as separate database lookup tables.

**Justification:**
- Every status value is tightly coupled to business logic. Adding a new `SeasonStatus`, for example, requires a corresponding change to the state-machine in `SeasonService` regardless. A separate lookup table would give no real flexibility — a new row still requires a code change.
- Java enums provide compile-time type safety; CHECK constraints enforce the same constraint at the DB level without extra JOINs.
- Lookup tables are appropriate when values are data-driven (admin-managed, growable without a deployment) or consumed independently by other systems. None of those conditions apply here.
- `app_config` is the only runtime-configurable reference store, and it is intentionally limited to tunable platform settings (lock offsets, JWT expiry, etc.).
- If a future requirement introduces data-driven reference data (e.g., sport types or notification channel types manageable via the admin UI), a dedicated lookup table should be introduced at that point and this entry updated.
