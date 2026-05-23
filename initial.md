## Overview
Build a Java Spring Boot application with two types of users:

- **Admins**
- **Users**

The platform allows admins to manage leagues, matches, players, and match results, while users can participate by submitting predictions and viewing predictions made by other users after the prediction window closes.

---

# User Roles

## Admin Features
Admins should be able to:

- Create and manage leagues and seasons
- Schedule and manage matches
- Add and manage teams and players
- Publish match results and league outcomes
- Close a league after verifying final results
- Bulk communicate to users by selecting recipients, event types, and custom messages
- Receive email alerts when results need to be updated

## User Features
Users should be able to:

- Update their profile (avatar name and profile picture)
- Predict the **full league standings** (positions 1 to n for all teams, not just the winner)
- Predict the **match winner**
- Predict the **toss winner**
- Predict the **player of the match**
- View predictions submitted by other users **only after the prediction window is closed**
- Receive email reminders if they haven't submitted predictions before the match closure
- View the leaderboard showing real-time scores and rankings

---

# Prediction Rules

## League Predictions
- Users predict the **full team standings** (1st place through last place) for the entire league
- League predictions must close **4 hours before the first match start time**
- Once closed, no edits are allowed

## Match Predictions
- Match winner, toss winner, and player of the match predictions must close **1 hour before the match start time**
- Once closed, no edits are allowed
- **Prediction lock must be enforced at the database level**, not just application logic

## Visibility
- Predictions are **private** until the prediction window closes
- After the window closes, users can view each other's predictions (head-to-head comparison)

## Prediction Window
- The prediction window opens from the moment the league is started
- It closes automatically based on the configured cutoff times

---

# League & Season Model

- A **League** is an umbrella concept (e.g., "IPL")
- A **Season** is a specific instance of a league (e.g., "IPL 2025")
- Teams are **independent of seasons** — the same team can participate in multiple seasons of the same league
- Each season has its own schedule, matches, and results

## League Lifecycle
1. Admin creates the league and season setup
2. League predictions open immediately
3. League predictions lock 4 hours before the first match start time
4. Match results are updated by admin after each match
5. League-level standings are adjusted after each match result
6. Admin updates final league results
7. Admin verifies results and **closes the league**
8. Closed leagues are **read-only** — no amendments allowed, not even by admin

---

# Scoring Rules

## Points
- Each **correct prediction earns 1 point**
- Applicable to: match winner, toss winner, player of the match, and league standings predictions
- **Tie results count as official outcomes** — both sides of a tied match earn 1 point
- Points are **never accepted via API** — the system always calculates them server-side (admin cannot override)

## Leaderboard
- Total points determine rank
- Rankings recalculate after each confirmed result
- Leaderboard recalculation runs **asynchronously**; admin is notified via email when complete

---

# Email Notifications

## User Notifications
- Reminder email sent to users who **have not submitted predictions** before the match closure window

## Admin Notifications
- Email alert when **results are pending update** after a match
- Email notification when **leaderboard recalculation completes**

## Bulk Communication
- Admin can select users and event types and send custom messages

## Email Storage
- All emails must be persisted in the database with:
  - Recipient
  - Event type / subject
  - Sent timestamp
  - Delivery status

---

# Configuration Management

All prediction cutoff timelines must be configurable and managed from the database — not hardcoded in the application.

Configurable values include:
- League prediction cutoff duration (default: 4 hours before first match)
- Match prediction cutoff duration (default: 1 hour before match start)
- Toss prediction cutoff duration
- Player of the match cutoff duration

These configurations are dynamically read by the application at runtime.

---

# Data Integrity & Audit

## Soft Delete
- No records are permanently deleted unless explicitly justified
- All deletions are **soft deletes** (e.g., `is_deleted` flag or `deleted_at` timestamp)
- Any exception to soft delete must be logged in the decision log with justification

## Audit Trail
- All data changes must be captured with standard audit fields:
  - `created_at`, `created_by`
  - `updated_at`, `updated_by`
  - `deleted_at`, `deleted_by` (for soft deletes)

---

# Security

- HTTPS required (local certs acceptable for development)
- JWT-based session management
- Role-based access control (RBAC) — admin routes must be protected
- Prediction lock enforced at the database level
- Points calculation is system-only — never accepted via API input

---

# Non-Functional Requirements

## Logging
- Console and file logging both required
- Appropriate log levels (INFO, WARN, ERROR, DEBUG) used consistently

## Exception Handling
- Centralised exception handling with consistent error response structure

## API Design
- APIs must be scoped to business needs — no single "do-everything" endpoint
- Consistent request and response data structures
- Pagination, search, and sort supported on all applicable list APIs

## Documentation
- OpenAPI / Swagger documentation for all APIs
- API collection (Postman or equivalent) included

## Decision Log
- Any architectural or design decision must be recorded with justification

## Modularity & Configurability
- Code must be modular to the cleanest possible limits
- Maximum configurability — avoid hardcoded values
