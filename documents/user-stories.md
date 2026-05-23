# User Stories — Family League

---

## Authentication & Registration

### US-001 — User Self-Registration
**As a** new user,
**I want to** register with my email, password, and display name,
**So that** I can access the platform and participate in leagues.

**Acceptance Criteria:**
- User provides email, password, and display name
- Password is stored as a hash (never plain text)
- Duplicate email registration is rejected with a clear error
- Successful registration returns a JWT token
- Account is created with `ROLE_USER` by default

---

### US-002 — Admin Account Creation
**As an** existing admin,
**I want to** create a new admin account by providing an email, password, and display name,
**So that** other trusted operators can manage the platform.

**Acceptance Criteria:**
- Only an authenticated admin can call this endpoint
- Admin provides email, password, and display name for the new admin
- Account is created with `ROLE_ADMIN`
- Duplicate email is rejected
- The first admin account is bootstrapped via a Flyway seed migration (not via API) — this must be documented in the decision log
- Password is hashed; plain text is never stored or returned

---

### US-003 — Login
**As a** registered user or admin,
**I want to** log in with my email and password,
**So that** I can access the platform.

**Acceptance Criteria:**
- Valid credentials return a signed JWT access token
- Invalid credentials return a 401 with a generic message (no hint about which field is wrong)
- Token contains `userId`, `role`, `iat`, and `exp`
- Token expiry duration is configurable via `app_config`

---

### US-004 — Token Refresh
**As an** authenticated user,
**I want to** refresh my session token before it expires,
**So that** I am not logged out mid-session.

**Acceptance Criteria:**
- A valid, non-expired token can be exchanged for a new one
- An expired token is rejected with 401
- Refresh does not require re-entering credentials

---

## User Profile

### US-005 — View My Profile
**As a** logged-in user,
**I want to** view my profile details,
**So that** I can confirm my account information is correct.

**Acceptance Criteria:**
- Returns display name, avatar URL, email, and role
- Does not expose password hash

---

### US-006 — Update My Profile
**As a** logged-in user,
**I want to** update my display name and profile picture (avatar),
**So that** other users can identify me in head-to-head comparisons.

**Acceptance Criteria:**
- User can update display name and avatar URL independently
- Changes are persisted immediately
- Email and role cannot be changed via this endpoint

---

## League & Season Management (Admin)

### US-007 — Create a League
**As an** admin,
**I want to** create a league with a name and description,
**So that** I can set up an umbrella structure for multiple seasons.

**Acceptance Criteria:**
- League requires a unique name
- Soft delete applies — leagues are never permanently removed
- Returns the created league with its generated ID

---

### US-008 — Create a Season
**As an** admin,
**I want to** create a season under an existing league,
**So that** I can run a specific instance of that league with its own schedule and participants.

**Acceptance Criteria:**
- Season is linked to a parent league
- Season starts in `DRAFT` status
- Multiple seasons can exist under the same league
- Returns the created season with its generated ID

---

### US-009 — Activate a Season
**As an** admin,
**I want to** activate a season after its setup is complete,
**So that** users can start submitting league predictions.

**Acceptance Criteria:**
- Only a season in `DRAFT` status can be activated
- After activation, status transitions to `OPEN`
- League prediction window opens immediately upon activation
- `league_lock_time` is calculated from `first_match_scheduled_at - league.lock.offset.hours` (from `app_config`) and stored on the season at this point
- A season with no teams or matches cannot be activated

---

### US-010 — Add Teams to a Season
**As an** admin,
**I want to** add existing teams to a season,
**So that** matches can be scheduled between them.

**Acceptance Criteria:**
- Teams must already exist (created independently of seasons)
- The same team can participate in multiple seasons
- A team cannot be added to the same season twice
- Returns the updated season team list

---

### US-011 — Remove a Team from a Season
**As an** admin,
**I want to** remove a team from a season,
**So that** I can correct setup mistakes before the season is activated.

**Acceptance Criteria:**
- Team can only be removed if the season is in `DRAFT` status
- Removal is a soft delete on the `season_teams` record
- Teams with already-scheduled matches in the season cannot be removed

---

### US-012 — Close a League Season
**As an** admin,
**I want to** close a season after verifying the final results,
**So that** the season becomes permanently read-only.

**Acceptance Criteria:**
- Only a season in `COMPLETED` status can be closed
- After closure, status transitions to `CLOSED`
- No entity within a closed season (predictions, results, standings) can be amended — including by admin
- Closed seasons remain accessible for viewing

---

## Team & Player Management (Admin)

### US-013 — Create a Team
**As an** admin,
**I want to** create a team with a name and optional logo,
**So that** it can be assigned to seasons and used in match scheduling.

**Acceptance Criteria:**
- Team name must be unique across the platform
- Logo URL is optional
- Team is independent of any season at creation time
- Soft delete applies

---

### US-014 — Add a Player to a Team
**As an** admin,
**I want to** add players to a team,
**So that** users can predict the player of the match.

**Acceptance Criteria:**
- Player requires a name; jersey number is optional
- Player is linked to exactly one team
- Players can be listed and filtered by team

---

## Match Scheduling (Admin)

### US-015 — Schedule a Match
**As an** admin,
**I want to** schedule a match between two teams in a season,
**So that** users know when to submit their predictions.

**Acceptance Criteria:**
- Match requires home team, away team, season, and scheduled start time
- Both teams must already be part of the season
- `lock_time` is auto-calculated as `scheduled_at - match.lock.offset.hours` (from `app_config`)
- Match starts in `SCHEDULED` status
- A team cannot be scheduled to play itself
- When the first match is scheduled in a season, `season.first_match_start_time` is updated

---

### US-016 — Update a Scheduled Match
**As an** admin,
**I want to** update the schedule time of a match,
**So that** I can reflect rescheduling from the actual real-world league.

**Acceptance Criteria:**
- Only `SCHEDULED` matches can be updated
- `lock_time` is recalculated automatically when `scheduled_at` changes
- `LOCKED` or `COMPLETED` matches cannot be modified

---

## Match Predictions (User)

### US-017 — Submit Match Predictions
**As a** user,
**I want to** predict the match winner, toss winner, and player of the match,
**So that** I can earn points if my predictions are correct.

**Acceptance Criteria:**
- All three prediction fields (winner, toss winner, POTM) are accepted in one submission
- Prediction is rejected if `now() >= match.lock_time` — enforced at service layer and by DB trigger
- A user can re-submit before lock to update their prediction (upsert behaviour)
- Only teams playing in the match are valid for winner and toss predictions
- Only players from teams playing in that match are valid for POTM prediction
- Prediction is tied to the authenticated user — cannot submit on behalf of another user

---

### US-018 — View My Match Predictions
**As a** user,
**I want to** view my own predictions for a match at any time,
**So that** I can review what I have submitted before the lock.

**Acceptance Criteria:**
- Returns the user's own prediction regardless of lock status
- Returns a clear empty/null state if the user hasn't submitted a prediction

---

### US-019 — View All Predictions After Lock
**As a** user,
**I want to** see what every user predicted for a match,
**So that** I can compare everyone's predictions once the window has closed.

**Acceptance Criteria:**
- Returns 403 if the match prediction window is still open
- After lock, returns all users' predictions for the match
- Paginated response

---

### US-020 — Head-to-Head Comparison
**As a** user,
**I want to** see a side-by-side view of all users' predictions for a match,
**So that** I can compare predictions competitively.

**Acceptance Criteria:**
- Only available after the match prediction window closes
- Shows each user's predicted winner, toss winner, and POTM in a single response
- Once results are published, highlights which predictions were correct

---

## League Predictions (User)

### US-021 — Submit League Standing Predictions
**As a** user,
**I want to** predict the full final standings of all teams in a season (positions 1 to n),
**So that** I can earn points for each team position I predict correctly.

**Acceptance Criteria:**
- User must assign a unique position (1 to n) to every team in the season — partial submissions are rejected
- Duplicate positions in a single submission are rejected
- Prediction is rejected if `now() >= season.league_lock_time` — enforced at service layer and by DB trigger
- A user can re-submit before lock to update their standings (upsert)

---

### US-022 — View League Predictions After Lock
**As a** user,
**I want to** see what positions other users predicted for each team,
**So that** I can compare league predictions after the window closes.

**Acceptance Criteria:**
- Returns 403 if the league prediction window is still open
- After lock, returns each user's full predicted standings table
- Paginated response

---

## Results (Admin)

### US-023 — Publish Match Result
**As an** admin,
**I want to** publish the result of a completed match,
**So that** the scoring engine can award points to users with correct predictions.

**Acceptance Criteria:**
- Admin provides: winning team (or draw flag), toss winner, player of the match
- Match status transitions to `COMPLETED`
- `league_standings` is updated to reflect the match outcome
- Scoring engine is triggered asynchronously — admin's API call returns immediately
- Admin receives an email when scoring and leaderboard recalculation complete
- Each user whose score changed receives an email with their points earned and current rank

---

### US-024 — View Match Result
**As a** user or admin,
**I want to** view the published result of a match,
**So that** I can see the official outcome.

**Acceptance Criteria:**
- Returns winner team, toss winner, player of the match, and draw flag
- Returns 404 if no result has been published yet for the match
- Available to all authenticated users

---

### US-025 — Publish Final League Result
**As an** admin,
**I want to** publish the final team standings for a season,
**So that** league-level prediction scores can be calculated and the season can be closed.

**Acceptance Criteria:**
- Admin confirms the final `league_standings` positions for all teams
- Triggers scoring of all `predictions_league` records (each correctly predicted position earns 1 point)
- Admin receives an email once final scoring completes
- Season status transitions to `COMPLETED`
- Season can then be closed via a separate close action (US-012)

---

## Scoring & Leaderboard

### US-026 — View User Prediction Leaderboard
**As a** user,
**I want to** see the ranked list of all users and their prediction points in a season,
**So that** I know how I am performing relative to others.

**Acceptance Criteria:**
- Returns users ranked by total points (descending)
- Equal points resolved by most recent correct prediction, then alphabetically
- Paginated and sortable by `rank` and `total_points`
- Reflects the most recently completed scoring run

---

### US-027 — View Real-World League Standings
**As a** user,
**I want to** see the actual team standings in the real league as matches are played,
**So that** I can track how the season is progressing.

**Acceptance Criteria:**
- Returns team position, matches played, wins, draws, losses, and league points per team
- Updated automatically after each match result is published
- Sortable by `current_position` and `points_in_league`
- Available throughout the season

---

## Notifications

### US-028 — Receive Match Prediction Reminder
**As a** user,
**I want to** receive an email reminder before a match prediction window closes,
**So that** I don't miss submitting my prediction.

**Acceptance Criteria:**
- Email sent only to users who have **not** submitted a prediction for that match
- Sent at a configurable time before the lock (driven by `match.reminder.offset.hours` in `app_config`)
- Email includes match name, teams, and the prediction deadline
- Email is logged to `email_log`

---

### US-029 — Admin Alerted for Pending Result (System Scheduled)
**As an** admin,
**I want to** receive an email alert when a match has ended but no result has been published,
**So that** I am reminded to update the result promptly.

**Acceptance Criteria:**
- Alert triggered by a scheduled job after `match.scheduled_at + result.pending.alert.hours` (configurable in `app_config`) and `match_result` does not exist
- Email lists all unpublished matches needing a result
- Email is logged to `email_log`
- Alert is not sent again once the result is published

---

### US-030 — User Receives Score Update After Result Published
**As a** user,
**I want to** receive an email after a match result is published showing my points earned,
**So that** I know how my predictions performed without having to check the leaderboard manually.

**Acceptance Criteria:**
- Email sent to every user who submitted predictions for that match
- Includes: points earned per prediction type (winner, toss, POTM), total points earned for the match, and current leaderboard rank in the season
- Sent after the async scoring engine completes (not before)
- Email is logged to `email_log`

---

### US-031 — Admin Notified When Leaderboard Recalculation Completes
**As an** admin,
**I want to** receive an email when the leaderboard has been recalculated after a result is published,
**So that** I know the scores are up to date.

**Acceptance Criteria:**
- Email sent after async scoring + leaderboard rebuild finishes
- Includes season name, match reference, and count of users whose scores were updated
- Email is logged to `email_log`

---

### US-032 — Bulk Communicate with Users (Admin)
**As an** admin,
**I want to** send a custom message to a selected group of users,
**So that** I can communicate announcements or event-specific information.

**Acceptance Criteria:**
- Admin selects recipient user IDs (or targets all users) and provides an event type and message body
- System dispatches individual emails per recipient
- Every email is logged to `email_log` regardless of success or failure
- Failed sends store an error message in `email_log`

---

### US-033 — View Email Log (Admin)
**As an** admin,
**I want to** view the history of all emails sent by the platform,
**So that** I can audit notification delivery and investigate failures.

**Acceptance Criteria:**
- Paginated list of all `email_log` records
- Filterable by `recipient_email`, `event_type`, `status` (SENT / FAILED / PENDING), and `sent_at` date range
- Sortable by `sent_at`

---

## System Behaviour (Implicit)

### US-034 — Automatic Match Prediction Lock
**As the** system,
**I need to** automatically lock match predictions at the configured time before each match,
**So that** no predictions are accepted after the cutoff.

**Acceptance Criteria:**
- A scheduled job sets `match.status = LOCKED` at `lock_time`
- Any prediction submission after `lock_time` is rejected at both service layer and by a DB trigger (not a CHECK constraint — PostgreSQL triggers are used since CHECK constraints cannot reference other tables)
- Lock is enforced even if the scheduled job is delayed — the DB trigger is the final guard

---

### US-035 — Automatic League Prediction Lock
**As the** system,
**I need to** automatically lock league standing predictions 4 hours before the first match,
**So that** users cannot change their league predictions once the season begins.

**Acceptance Criteria:**
- `season.status` transitions from `OPEN` to `LOCKED` at `league_lock_time`
- Any league prediction submission after this time is rejected at service layer and by DB trigger
- `league_lock_time` is derived from `first_match_scheduled_at - league.lock.offset.hours`

---

### US-036 — Asynchronous Score Calculation
**As the** system,
**I need to** calculate user scores in the background after a result is published,
**So that** result publishing is fast and scoring does not block the admin's API response.

**Acceptance Criteria:**
- Admin's publish result API returns immediately after saving the result
- Scoring job runs asynchronously and processes all predictions for that match
- Points are written only by the scoring engine — never via any API input, including admin
- On completion: admin receives leaderboard recalculation email (US-031) and each user receives score update email (US-030)
- If scoring fails, the error is logged and admin is notified separately

---

## Configuration (Admin)

### US-037 — View and Update App Configuration
**As an** admin,
**I want to** view and update platform configuration values,
**So that** I can tune timing and behaviour without a code deployment.

**Acceptance Criteria:**
- `GET /config` returns all key-value pairs with descriptions
- `PUT /config/{key}` updates a single value
- Unknown keys are rejected
- Updated values take effect immediately (cache evicts on update)
- Config changes are logged (who changed what and when)
