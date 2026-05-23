# Family League

A Spring Boot backend for a family-and-friends sports prediction platform. Users predict match winners, toss results, and player of the match. Points are awarded for correct predictions and a leaderboard tracks rankings across the season.

---

## Documentation

| Document | Description |
|---|---|
| [Business Requirements](documents/family-league-requirements.md) | Full functional and non-functional requirements |
| [Technical Requirements](documents/technical-requirements.md) | Technology stack, API design standards, and constraints |
| [User Stories](documents/user-stories.md) | Feature breakdown by persona |
| [Data Model](documents/data-model.md) | Entity descriptions and relationships |
| [ER Diagram](documents/erDiagram.mmd) | Mermaid ERD — open in any Mermaid-compatible viewer |
| [Decision Log](documents/decision-log.md) | All architectural and design decisions with justifications |
| [AI Prompt Summary](documents/prompt-summary.md) | Prompts submitted to Claude Code during development |

---

## Technology Stack

| Layer | Choice |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Migrations | Flyway |
| Auth | Spring Security 6 + JWT |
| Build | Maven |

---

## Prerequisites

- Java 17+
- Maven 3.8+ (or use the included `./mvnw` wrapper — no installation needed)
- PostgreSQL 14+

---

## Setup

### 1. Create the database

```sql
CREATE DATABASE practice_java;
```

Connect as the `postgres` user (default). If your local `postgres` user has no password, set one first:

```bash
psql postgres -c "ALTER USER postgres PASSWORD 'postgres';"
```

### 2. Clone and configure

```bash
git clone <repo-url>
cd practice-java
```

The default datasource in `src/main/resources/application.properties` connects to:

```
Host:     localhost:5432
Database: practice_java
Username: your_username
Password: your_password
```

To override without editing the file, export environment variables before running:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_db
export SPRING_DATASOURCE_USERNAME=your_user
export SPRING_DATASOURCE_PASSWORD=your_password
```

### 3. Configure email (optional)

Email sending uses SMTP. Set these environment variables to enable it:

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM=noreply@familyleague.app
```

If these are not set, the application starts normally but email delivery will fail silently.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

Or build and run the jar:

```bash
./mvnw clean package -DskipTests
java -jar target/practice-java-0.0.1-SNAPSHOT.jar
```

Flyway runs all migrations automatically on startup. The schema and seed data are created from the scripts in `src/main/resources/db/migration/`.

The server starts on **http://localhost:8080**.

---

## Default Admin Account

A seed admin account is created by the `V11__seed_admin.sql` migration:

| Field | Value |
|---|---|
| Email | `admin@gmail.com` |
| Password | `Admin@1234` |
| Role | `ADMIN` |

Use this account to obtain a JWT token and access admin-only endpoints.

---

## Authentication

All endpoints except `/api/v1/auth/register` and `/api/v1/auth/login` require a Bearer token.

**Login:**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@gmail.com",
  "password": "Admin@1234"
}
```

The response includes a `token`. Pass it in subsequent requests:
```
Authorization: Bearer <token>
```

---

## API Reference

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

All endpoints are under the `/api/v1` prefix and return a consistent envelope:

```json
{
  "success": true,
  "data": {},
  "error": null,
  "timestamp": "2025-01-01T00:00:00Z"
}
```

---

## Key Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/practice_java` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | DB password |
| `JWT_SECRET` | *(default in properties)* | JWT signing secret — **change in production** |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | *(empty)* | SMTP username |
| `MAIL_PASSWORD` | *(empty)* | SMTP password |
| `MAIL_FROM` | `noreply@familyleague.app` | Sender address |

---

## Database Migrations

All schema changes are managed by Flyway. Migration scripts live in:

```
src/main/resources/db/migration/
```

| Script | Description |
|---|---|
| V1 | Users table |
| V2 | Leagues and seasons |
| V3 | Teams and players |
| V4 | Matches |
| V5 | League standings |
| V6 | Predictions |
| V7 | Scoring and leaderboard |
| V8 | App config |
| V9 | Email log |
| V10 | Seed app config defaults |
| V11 | Seed admin account |
| V12 | Seed sample IPL data |
| V13 | Add team name to players |
| V14 | Notifications |
| V15 | Prediction lock triggers |

---

## Project Structure

```
src/main/java/com/example/practicejava/
├── auth/               # Registration, login, JWT filter, security config
├── common/             # BaseEntity, response envelope, exception handling, validation
├── league/             # League and Season entities, controllers, services
├── match/              # Match scheduling and result publishing
├── notification/       # Email log, bulk communication
├── player/             # Player management
├── prediction/         # Match and league predictions
├── scoring/            # Score engine, leaderboard, score breakdowns
└── team/               # Team management
```
