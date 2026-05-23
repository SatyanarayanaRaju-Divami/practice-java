# Technical Requirements — Family League

Version 1.0

---

## 1. Technology Stack

| Layer | Choice | Notes |
|---|---|---|
| Language | Java 17+ | LTS release |
| Framework | Spring Boot 3.x | Web, Security, Data, Validation, Mail, Scheduling |
| ORM | Spring Data JPA + Hibernate | Standard persistence layer |
| Database | PostgreSQL | Primary datastore; MySQL is acceptable if chosen — must be justified in decision log |
| Migrations | Flyway | Versioned schema management |
| Auth | Spring Security + JWT | Stateless token-based auth |
| Validation | Spring Validation (`@Valid`, `@Validated`) | Request-level bean validation |
| Email | Spring Mail (SMTP) | Notification delivery |
| Scheduling | Spring `@Scheduled` / Quartz | Time-driven jobs |
| API Docs | SpringDoc OpenAPI (Swagger UI) | Auto-generated from annotations |
| Build | Maven or Gradle | Either acceptable; choose one and justify in decision log |
| Logging | Logback (via SLF4J) | Console + rolling file appender |
| Caching | Spring Cache (e.g., Caffeine) | Good-to-have; only with a safe eviction policy |
| Batch | Spring Batch (optional) | Good-to-have; for bulk email dispatch and score calculations |

---

## 2. Project Structure

Follow a layered, modular package structure:

```
com.familyleague
├── auth            # Authentication, JWT filter, token utilities
├── config          # App config, security config, scheduler config
├── common          # Base entities, audit, soft delete, exceptions, response wrappers
├── user            # User entity, profile, CRUD
├── league          # League + Season management
├── team            # Team and player management
├── match           # Match scheduling, results
├── prediction      # Prediction submission and retrieval
├── scoring         # Points calculation, leaderboard
├── notification    # Email service, email log, scheduled reminders
└── admin           # Admin-only APIs (bulk communication, result publishing)
```

Each module owns its controller, service (interface + implementation), repository, and DTOs.

---

## 3. Authentication & Authorization

### 3.1 Authentication
- Simple username/password login — OAuth (Google) is a good-to-have
- On successful login, issue a signed **JWT access token**
- Token contains: `userId`, `role`, `iat`, `exp`
- Token expiry and secret must be **externally configurable** (application properties / DB config)
- Refresh token support is optional but preferred

### 3.2 Role-Based Access Control (RBAC) and ACL
Two roles: `ROLE_ADMIN` and `ROLE_USER`

ACL rules layer on top of RBAC for data-scoped access:
- A user can only edit or delete **their own** predictions
- A user can only view **their own** prediction before the lock window closes; after lock, all predictions for that match/season are visible
- Admin can read all data but cannot amend closed leagues or override point calculations

| Resource | ROLE_USER | ROLE_ADMIN |
|---|---|---|
| Register / Login | ✓ | ✓ |
| View profile | ✓ | ✓ |
| Update own profile | ✓ | ✓ |
| Submit predictions | ✓ | — |
| View predictions (post-lock) | ✓ | ✓ |
| View leaderboard | ✓ | ✓ |
| Create / manage leagues | — | ✓ |
| Create / manage matches | — | ✓ |
| Publish results | — | ✓ |
| Close league | — | ✓ |
| Bulk communicate | — | ✓ |
| View email logs | — | ✓ |

- All admin routes must be protected at both the Spring Security filter and method level (`@PreAuthorize`)
- Points are **never writable via API** — they are calculated server-side only

### 3.3 Security Config
- Stateless session (`SessionCreationPolicy.STATELESS`)
- JWT filter runs before `UsernamePasswordAuthenticationFilter`
- HTTPS enforced; local self-signed certs acceptable in development
- No sensitive data (passwords, tokens) logged anywhere

---

## 4. Data Model

### 4.1 Base Entity (inherited by all entities)

```
BaseEntity
  id          UUID (PK)
  created_at  TIMESTAMP NOT NULL
  created_by  UUID (FK → users)
  updated_at  TIMESTAMP
  updated_by  UUID (FK → users)
  deleted_at  TIMESTAMP  -- NULL means active
  deleted_by  UUID (FK → users)
  is_deleted  BOOLEAN DEFAULT FALSE
```

All entities extend `BaseEntity`. Spring Data `AuditorAware` auto-populates audit fields from the JWT principal.

### 4.2 Core Tables

**users**
- id, email, password_hash, display_name, avatar_url, role, is_active

**leagues**
- id, name, description

**seasons** (instance of a league)
- id, league_id, name, status (DRAFT | OPEN | LOCKED | COMPLETED | CLOSED), first_match_start_time, league_lock_time (derived), created by admin

**teams**
- id, name, logo_url (teams are independent of seasons)

**season_teams** (join table)
- season_id, team_id, seed_position

**players**
- id, team_id, name, jersey_number

**matches**
- id, season_id, home_team_id, away_team_id, scheduled_at, lock_time (scheduled_at - configured offset), status (SCHEDULED | LOCKED | COMPLETED)

**match_results**
- id, match_id, winner_team_id, toss_winner_team_id, player_of_match_id, is_draw, published_at, published_by

**league_standings** _(tracks actual real-world team positions as matches progress — distinct from user prediction leaderboard)_
- id, season_id, team_id, current_position, matches_played, wins, losses, draws, points_in_league
- Updated after each match result is published; used to determine final team positions for scoring league predictions

**predictions_league**
- id, season_id, user_id, team_id, predicted_position, submitted_at

**predictions_match**
- id, match_id, user_id, predicted_winner_team_id, predicted_toss_winner_team_id, predicted_potm_player_id, submitted_at

**scores**
- id, season_id, user_id, total_points (system-calculated only — never written by API)

**score_breakdown**
- id, score_id, match_id / season_id, prediction_type (MATCH_WINNER | TOSS | POTM | LEAGUE_STANDING), points_earned

**leaderboard** _(user prediction leaderboard — separate from league_standings)_
- id, season_id, user_id, rank, total_points, last_calculated_at

**app_config**
- key (VARCHAR, PK), value, description (stores cutoff durations and other runtime config)

**email_log**
- id, recipient_user_id, recipient_email, event_type, subject, body_summary, status (SENT | FAILED | PENDING), sent_at, error_message

### 4.3 JPA Auditing (Auto-population)

Enable Spring Data JPA Auditing via `@EnableJpaAuditing` on the application config class. All `BaseEntity` fields are auto-populated:

- `@CreatedDate` → `created_at`
- `@LastModifiedDate` → `updated_at`
- `@CreatedBy` → `created_by` (resolved via `AuditorAware<UUID>` bean that reads the current JWT principal)
- `@LastModifiedBy` → `updated_by`

This removes the need for manual field assignment in every service.

### 4.4 Soft Delete Strategy
- All DELETE operations set `is_deleted = true` and populate `deleted_at` / `deleted_by`
- JPA `@SQLRestriction("is_deleted = false")` (Boot 3.x) on all entities to filter deleted records automatically
- Any permanent delete requires a decision log entry

---

## 5. API Design

### 5.1 Admin API as an Operational Guide

The API collection and OpenAPI documentation must together be sufficient for an admin persona to operate the platform without any external guide. This means:
- Every admin workflow (create league → add season → add teams → schedule matches → publish results → close league) must be individually documented with request/response examples
- The Postman collection must be organised into folders matching the admin workflow sequence
- OpenAPI descriptions must include business context, not just field types

### 5.2 Conventions
- Base path: `/api/v1`
- JSON request and response bodies everywhere
- Consistent response envelope:
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2025-05-21T10:00:00Z"
}
```
- Error responses include `code`, `message`, and `fieldErrors` (for validation failures)
- All list endpoints support: `page`, `size`, `sort` (field + direction), and relevant `search` / `filter` query params

**Search and filter fields per endpoint:**

| Endpoint | Searchable / Filterable fields |
|---|---|
| `GET /leagues` | `name` (contains) |
| `GET /leagues/{id}/seasons` | `status`, `name` |
| `GET /teams` | `name` (contains) |
| `GET /teams/{id}/players` | `name` (contains) |
| `GET /seasons/{id}/matches` | `status`, `scheduled_at` (date range), `home_team_id`, `away_team_id` |
| `GET /users` | `email`, `display_name`, `role` |
| `GET /notifications/emails` | `status`, `event_type`, `recipient_email`, `sent_at` (date range) |
| `GET /seasons/{id}/leaderboard` | sortable by `rank`, `total_points` |
| `GET /seasons/{id}/standings` | sortable by `current_position`, `points_in_league` |

### 5.3 Endpoint Groups

#### Auth
| Method | Path | Access |
|---|---|---|
| POST | `/auth/register` | Public |
| POST | `/auth/login` | Public |
| POST | `/auth/refresh` | Authenticated |

#### User / Profile
| Method | Path | Access |
|---|---|---|
| GET | `/users/me` | USER, ADMIN |
| PUT | `/users/me` | USER, ADMIN |
| GET | `/users` | ADMIN |

#### League & Season
| Method | Path | Access |
|---|---|---|
| POST | `/leagues` | ADMIN |
| GET | `/leagues` | USER, ADMIN |
| GET | `/leagues/{leagueId}` | USER, ADMIN |
| POST | `/leagues/{leagueId}/seasons` | ADMIN |
| GET | `/leagues/{leagueId}/seasons` | USER, ADMIN |
| GET | `/seasons/{seasonId}` | USER, ADMIN |
| PUT | `/seasons/{seasonId}/activate` | ADMIN — transitions DRAFT → OPEN |
| PUT | `/seasons/{seasonId}/close` | ADMIN — transitions COMPLETED → CLOSED |

#### Teams & Players
| Method | Path | Access |
|---|---|---|
| POST | `/teams` | ADMIN |
| GET | `/teams` | USER, ADMIN |
| GET | `/teams/{teamId}` | USER, ADMIN |
| POST | `/teams/{teamId}/players` | ADMIN |
| GET | `/teams/{teamId}/players` | USER, ADMIN |
| GET | `/players/{playerId}` | USER, ADMIN |
| POST | `/seasons/{seasonId}/teams` | ADMIN |
| DELETE | `/seasons/{seasonId}/teams/{teamId}` | ADMIN — soft removes team from season |

#### Matches
| Method | Path | Access |
|---|---|---|
| POST | `/seasons/{seasonId}/matches` | ADMIN |
| GET | `/seasons/{seasonId}/matches` | USER, ADMIN |
| GET | `/matches/{matchId}` | USER, ADMIN |
| PUT | `/matches/{matchId}` | ADMIN |

#### Results
| Method | Path | Access |
|---|---|---|
| POST | `/matches/{matchId}/result` | ADMIN |
| GET | `/matches/{matchId}/result` | USER, ADMIN |
| PUT | `/seasons/{seasonId}/result` | ADMIN |

#### Predictions
| Method | Path | Access |
|---|---|---|
| POST | `/seasons/{seasonId}/predictions/league` | USER |
| GET | `/seasons/{seasonId}/predictions/league` | USER (post-lock only) |
| POST | `/matches/{matchId}/predictions` | USER |
| GET | `/matches/{matchId}/predictions` | USER (post-lock only) |
| GET | `/matches/{matchId}/predictions/me` | USER |
| GET | `/matches/{matchId}/predictions/head-to-head` | USER (post-lock only) |

The head-to-head endpoint returns a side-by-side view of all users' predictions for a given match. Only available after the match prediction window closes.

#### League Standings (actual team positions)
| Method | Path | Access |
|---|---|---|
| GET | `/seasons/{seasonId}/standings` | USER, ADMIN |

Returns the real-world team position table for the season (updated after each match result). This is distinct from the user prediction leaderboard.

#### User Prediction Leaderboard
| Method | Path | Access |
|---|---|---|
| GET | `/seasons/{seasonId}/leaderboard` | USER, ADMIN |

#### App Config (Admin)
| Method | Path | Access |
|---|---|---|
| GET | `/config` | ADMIN |
| PUT | `/config/{key}` | ADMIN |

#### Notifications (Admin)
| Method | Path | Access |
|---|---|---|
| POST | `/notifications/bulk` | ADMIN |
| GET | `/notifications/emails` | ADMIN |

---

## 6. Prediction Lock Enforcement

- `lock_time` is stored on each `match` record and computed at creation: `scheduled_at - match_lock_offset`
- League lock time is `first_match_scheduled_at - league_lock_offset`
- On any prediction submission, the service layer checks `now() < lock_time`
- After lock, the `match.status` transitions to `LOCKED` via a scheduled job

### 6.1 DB-Level Lock Enforcement (Trigger)

PostgreSQL `CHECK` constraints cannot reference another table, so DB-level enforcement is implemented as a `BEFORE INSERT OR UPDATE` trigger on `predictions_match` and `predictions_league`:

```sql
-- Example for predictions_match
CREATE OR REPLACE FUNCTION check_match_prediction_lock()
RETURNS TRIGGER AS $$
DECLARE
    v_lock_time TIMESTAMP WITH TIME ZONE;
BEGIN
    SELECT lock_time INTO v_lock_time FROM matches WHERE id = NEW.match_id;
    IF now() >= v_lock_time THEN
        RAISE EXCEPTION 'Prediction window is closed for match %', NEW.match_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_match_prediction_lock
BEFORE INSERT OR UPDATE ON predictions_match
FOR EACH ROW EXECUTE FUNCTION check_match_prediction_lock();
```

A corresponding trigger is added for `predictions_league` checking against `season.league_lock_time`. Both triggers are added in a dedicated Flyway migration (`V11__add_prediction_lock_triggers.sql`).

---

## 7. Scoring Engine

- Scoring runs **asynchronously** after admin publishes a result (via Spring `@Async` or an event listener)
- `ScoreCalculationService` reads the published result and iterates all predictions for that match/season
- For each correct prediction: `score_breakdown` entry inserted, `scores.total_points` incremented
- Tie handling: if `match_result.is_draw = true`, all users who predicted either team as winner receive 1 point
- Points are **never written by any external API call** — only `ScoreCalculationService` writes to `scores`
- After calculation completes, admin receives an email notification
- Leaderboard is rebuilt from `scores` table and `leaderboard` table is updated

### 7.1 Dual Leaderboard Distinction

There are **two separate leaderboard concepts** that must not be conflated:

| Concept | Table | What it tracks | Updated when |
|---|---|---|---|
| **League standings** | `league_standings` | Actual real-world team positions in the season | After each match result is published |
| **User prediction leaderboard** | `leaderboard` | User points from correct predictions | After scoring engine runs |

The `league_standings` table is updated first (part of result processing). Once final team positions are confirmed at season end, `ScoreCalculationService` uses them to score league-level predictions (did the user correctly predict team positions 1-to-n?).

### 7.2 League Prediction Scoring
- League prediction scoring runs only when the admin publishes the **final league result** and closes the season
- Each `predictions_league` row is compared to the final `league_standings` positions
- A matching `predicted_position = current_position` earns 1 point per team correctly placed

### 7.3 Scoring Rule Decision
The requirements state "One Prediction adds one point to the user score in the league." This is interpreted as **one correct prediction = one point** (not one point per submission regardless of correctness). Rationale: awarding points for incorrect predictions removes all competitive value. This decision must be recorded in the decision log.

---

## 8. Notification System

### 8.1 Email Service
- `EmailService` interface with SMTP implementation (Spring Mail)
- All outgoing emails are logged to `email_log` before and after send (status updated on success/failure)

### 8.2 Scheduled Jobs

| Job | Trigger | Action |
|---|---|---|
| Match prediction reminder | Configurable time before lock (e.g., 2 hrs) | Find users with no prediction for upcoming match → send reminder email |
| Match lock enforcement | At `lock_time` of each match | Set `match.status = LOCKED` |
| League lock enforcement | At league lock time | Set `season.status = LOCKED` |
| Admin result pending alert | Configurable window after `match.scheduled_at` passes with no published result | Send alert email to admin listing unpublished matches |

- Jobs use Spring `@Scheduled` with cron or fixed-delay expressions
- Job intervals and trigger windows are configurable via `app_config` table

### 8.3 User Notifications After Result Published
After admin publishes a match result and the scoring engine completes:
- Each user whose score changed receives an email summarising their points earned for that match and their current leaderboard rank
- This fires as part of the async result processing pipeline, after `ScoreCalculationService` completes
- Email is logged to `email_log` per recipient

### 8.4 Bulk Communication
- Admin selects target users, event type, and custom message body
- System sends emails and logs each one to `email_log`

---

## 9. Configuration

All runtime-tunable values stored in `app_config` table:

| Key | Default | Description |
|---|---|---|
| `match.lock.offset.hours` | `1` | Hours before match start to lock predictions |
| `league.lock.offset.hours` | `4` | Hours before first match to lock league predictions |
| `match.reminder.offset.hours` | `2` | When to send reminder emails before match lock |
| `result.pending.alert.hours` | `2` | Hours after match scheduled_at to alert admin if result not yet published |
| `jwt.expiry.seconds` | `86400` | JWT token TTL |
| `leaderboard.recalc.async` | `true` | Whether leaderboard recalc runs async |

- A `ConfigService` reads these at startup and caches them; cache evicts when config is updated via admin API

---

## 10. Logging

- Framework: Logback via SLF4J
- **Console appender**: human-readable pattern for development
- **File appender**: rolling daily file, retained for 30 days
- Log levels: `ERROR` for exceptions, `WARN` for business rule violations, `INFO` for significant events (login, result published), `DEBUG` for internal flow (disabled in production)
- Never log passwords, JWT tokens, or PII beyond email address

---

## 11. Exception Handling

- Global `@RestControllerAdvice` handles all exceptions
- Custom exception hierarchy:
  - `AppException` (base)
  - `ResourceNotFoundException` → 404
  - `PredictionLockedException` → 409
  - `AccessDeniedException` → 403
  - `ValidationException` → 400
- Validation errors from `@Valid` return field-level error messages
- Unexpected exceptions return a generic 500 with a correlation ID logged server-side

---

## 12. Database Migrations

- Flyway manages all schema changes
- Migration files: `src/main/resources/db/migration/V{n}__{description}.sql`
- No manual schema changes — all DDL goes through Flyway
- Flyway runs automatically on application startup

---

## 13. OpenAPI Documentation

- SpringDoc OpenAPI generates Swagger UI at `/swagger-ui.html`
- All endpoints annotated with `@Operation`, `@ApiResponse`, and `@Tag`
- API collection (Postman) exported and committed to repo under `/docs/api-collection.json`
- Swagger UI secured behind `ROLE_ADMIN` or disabled in production (configurable)

---

## 14. Good-to-Have Features

### Unit Tests
- JUnit 5 + Mockito
- Coverage targets: service layer at minimum
- Focus on scoring engine, lock enforcement, and notification logic

### Integration / E2E Tests
- `@SpringBootTest` with Testcontainers (PostgreSQL)
- Tests cover prediction submission, lock enforcement, result publishing, and leaderboard update flows

### Batch Processing
- Use Spring Batch (or Spring `@Async` task executor) for operations that fan out to many records:
  - Bulk email dispatch (e.g., reminder emails to all users before match lock)
  - Scoring calculation across all predictions after a result is published
- Batch jobs must be idempotent and restartable

### Caching
- Use Spring Cache (backed by Caffeine or Redis) only on read-heavy, low-volatility data:
  - `app_config` entries
  - League/season listings
  - Leaderboard snapshots (with TTL-based or event-driven eviction)
- Every cached method must have an explicit eviction strategy (`@CacheEvict` or TTL)
- If caching is introduced, document the eviction policy in the decision log

---

## 15. Non-Functional Standards

- **Interface-driven development**: all service classes implement an interface; controllers depend on interfaces
- **No hardcoded values**: credentials, URLs, and timeouts via `application.properties` / environment variables
- **Normalization**: database schema normalized to 3NF minimum
- **Pagination**: all list endpoints paginated; default page size configurable
- **Decision log**: `docs/decision-log.md` records every significant architectural choice with justification
- **No hardcoded credentials** in source code or config files committed to the repo
- **Inline code documentation**: JavaDoc on all public methods and classes where the intent is not obvious from the name alone; avoid over-commenting trivial code
- **Java and Spring coding standards**: follow standard Java naming conventions, Spring idioms (constructor injection over field injection, `@Transactional` at service layer, no logic in controllers), and general JEE best practices
- **Spring Validation**: use `@Valid` / `@Validated` with constraint annotations (`@NotNull`, `@Size`, etc.) on all request DTOs; never validate manually in service code what a constraint annotation can express
- **Spring Data repositories**: use Spring Data repository interfaces (`JpaRepository`, `PagingAndSortingRepository`) for all data access; custom queries via `@Query` or Specifications — no raw JDBC unless justified in decision log

---

## 16. Submission Checklist

- [ ] App starts from clean clone following README steps
- [ ] README links to all documentation
- [ ] All docs live within the repo under `/docs`
- [ ] OpenAPI docs accessible on startup
- [ ] Postman collection committed
- [ ] Decision log present and up to date
- [ ] AI prompt composition log present (tool used + prompts)
- [ ] No hardcoded credentials or personal data in codebase
- [ ] Gopal and Rama added as GitHub repo moderators
- [ ] All assessable via `main` branch only
