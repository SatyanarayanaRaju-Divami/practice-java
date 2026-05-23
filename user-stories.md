# User Stories — Family League

---

## Authentication & Registration

### US-001 — User Registration
**As a** new user,
**I want to** register with my email and password,
**So that** I can access the platform and participate in leagues.

**Acceptance Criteria:**
- User provides email, password, and display name
- Password is stored as a hash (never plain text)
- Duplicate email registration is rejected with a clear error
- Successful registration returns a JWT token
- Account is created with `ROLE_USER` by default

---

### US-002 — User Login
**As a** registered user,
**I want to** log in with my email and password,
**So that** I can access my predictions and the leaderboard.

**Acceptance Criteria:**
- Valid credentials return a signed JWT access token
- Invalid credentials return a 401 with a generic message (no hint about which field is wrong)
- Token contains userId, role, issued-at, and expiry
- Token expiry duration is configurable

---

### US-003 — Token Refresh
**As an** authenticated user,
**I want to** refresh my session token before it expires,
**So that** I am not logged out mid-session.

**Acceptance Criteria:**
- A valid, non-expired token can be exchanged for a new one
- An expired token is rejected
- Refresh does not require re-entering credentials

---

## User Profile

### US-004 — View My Profile
**As a** logged-in user,
**I want to** view my profile details,
**So that** I can confirm my account information is correct.

**Acceptance Criteria:**
- Returns display name, avatar URL, and email
- Does not expose password hash or internal IDs

---

### US-005 — Update My Profile
**As a** logged-in user,
**I want to** update my display name and profile picture,
**So that** other users can identify me in head-to-head comparisons.

**Acceptance Criteria:**
- User can update display name and avatar URL independently
- Changes are persisted immediately
- Email cannot be changed via this endpoint

---

## League & Season Management (Admin)

### US-006 — Create a League
**As an** admin,
**I want to** create a league with a name and description,
**So that** I can set up an umbrella structure for multiple seasons.

**Acceptance Criteria:**
- League requires a unique name
- League is created in an active state
- Soft delete applies — leagues are never permanently removed

---

### US-007 — Create a Season
**As an** admin,
**I want to** create a season under an existing league,
**So that** I can run a specific instance of the league with its own schedule and participants.

**Acceptance Criteria:**
- Season is linked to a parent league
- Season starts in `DRAFT` status
- Season becomes `OPEN` once the admin activates it
- Multiple seasons can exist under the same league

---

### US-008 — Add Teams to a Season
**As an** admin,
**I want to** add existing teams to a season,
**So that** matches can be scheduled between them.

**Acceptance Criteria:**
- Teams are reusable across seasons (created independently)
- The same team can participate in multiple seasons
- A team cannot be added to the same season twice
- Removing a team from a season is a soft operation

---

### US-009 — Close a League Season
**As an** admin,
**I want to** close a season after verifying the final results,
**So that** the season becomes read-only and the final leaderboard is locked.

**Acceptance Criteria:**
- Only a season in `COMPLETED` or `OPEN` status can be closed
- After closure, status transitions to `CLOSED`
- No entity within a closed season (predictions, results, standings) can be amended — including by admin
- Closed seasons remain accessible for viewing

---

## Team & Player Management (Admin)

### US-010 — Create a Team
**As an** admin,
**I want to** create a team with a name and logo,
**So that** it can be assigned to seasons and used in match scheduling.

**Acceptance Criteria:**
- Team name must be unique
- Logo URL is optional
- Team is independent of any season at creation time

---

### US-011 — Add a Player to a Team
**As an** admin,
**I want to** add players to a team,
**So that** users can predict the player of the match.

**Acceptance Criteria:**
- Player requires a name and optional jersey number
- Player is linked to exactly one team
- Players can be listed and filtered by team

---

## Match Scheduling (Admin)

### US-012 — Schedule a Match
**As an** admin,
**I want to** schedule a match between two teams in a season,
**So that** users know when to submit their predictions.

**Acceptance Criteria:**
- Match requires home team, away team, season, and scheduled start time
- Both teams must already be part of the season
- `lock_time` is auto-calculated as `scheduled_at - match_lock_offset` (from config)
- Match starts in `SCHEDULED` status
- A team cannot play itself

---

### US-013 — Update a Scheduled Match
**As an** admin,
**I want to** update the schedule time of a match,
**So that** I can reflect rescheduling from the actual league.

**Acceptance Criteria:**
- Only `SCHEDULED` matches can be updated
- `lock_time` is recalculated automatically when `scheduled_at` changes
- `LOCKED` or `COMPLETED` matches cannot be modified

---

## Match Predictions (User)

### US-014 — Submit Match Predictions
**As a** user,
**I want to** predict the match winner, toss winner, and player of the match,
**So that** I can earn points if my predictions are correct.

**Acceptance Criteria:**
- All three prediction fields (winner, toss winner, POTM) are accepted in one submission
- Prediction is rejected if the current time is at or past the match `lock_time`
- A user can re-submit before lock to update their prediction (upsert behaviour)
- Only teams playing in the match are valid for winner/toss predictions
- Only players from teams playing in the match are valid for POTM prediction
- Prediction is tied to the authenticated user — cannot submit on behalf of another user

---

### US-015 — View My Match Predictions
**As a** user,
**I want to** view my own predictions for a match at any time,
**So that** I can review what I have submitted.

**Acceptance Criteria:**
- Returns the user's own prediction regardless of lock status
- Returns a clear "no prediction submitted" state if the user hasn't predicted

---

### US-016 — View All Predictions After Lock
**As a** user,
**I want to** see what other users predicted for a match,
**So that** I can compare predictions once the window has closed.

**Acceptance Criteria:**
- Endpoint is rejected (403 or 409) if the match prediction window is still open
- After lock, returns all users' predictions for the match
- Paginated response

---

### US-017 — Head-to-Head Comparison
**As a** user,
**I want to** see a side-by-side comparison of all users' predictions for a match,
**So that** I can see who predicted what in a competitive view.

**Acceptance Criteria:**
- Only available after the match prediction window closes
- Shows each user's predicted winner, toss winner, and POTM in a single response
- Highlights correct predictions once results are published

---

## League Predictions (User)

### US-018 — Submit League Standing Predictions
**As a** user,
**I want to** predict the full final standings of all teams in a season (1st to last),
**So that** I can earn points for correctly predicting team positions.

**Acceptance Criteria:**
- User must assign a unique position (1 to n) to every team in the season
- Partial submissions (not all teams ranked) are rejected
- Duplicate positions in a single submission are rejected
- Prediction is rejected if the current time is at or past the season `league_lock_time`
- A user can re-submit before lock to update their rankings (upsert)

---

### US-019 — View League Standing Predictions After Lock
**As a** user,
**I want to** see what positions other users predicted for each team,
**So that** I can compare league predictions after the window closes.

**Acceptance Criteria:**
- Only available after the league prediction window closes
- Returns each user's full predicted standings table
- Paginated response

---

## Results (Admin)

### US-020 — Publish Match Result
**As an** admin,
**I want to** publish the result of a completed match,
**So that** the scoring engine can calculate points for that match.

**Acceptance Criteria:**
- Admin provides: winning team (or draw flag), toss winner, player of the match
- Match status transitions to `COMPLETED`
- Scoring engine is triggered asynchronously after publish
- `league_standings` is updated to reflect the match outcome
- Admin receives an email confirmation once scoring and leaderboard recalculation complete

---

### US-021 — Publish Final League Result
**As an** admin,
**I want to** publish the final team standings for a season,
**So that** league-level prediction scores can be calculated.

**Acceptance Criteria:**
- Admin confirms the final `league_standings` positions for all teams
- Triggers scoring of all `predictions_league` records
- Admin receives an email once final scoring completes
- Season status transitions to `COMPLETED`

---

## Scoring & Leaderboard

### US-022 — View User Prediction Leaderboard
**As a** user,
**I want to** see the ranked list of users and their total prediction points in a season,
**So that** I know how I am performing relative to others.

**Acceptance Criteria:**
- Returns users ranked by total points (descending)
- Equal points → secondary sort by most recent correct prediction (or alphabetical as fallback)
- Paginated and sortable
- Leaderboard reflects the most recent completed scoring run

---

### US-023 — View Real-World League Standings
**As a** user,
**I want to** see the actual team standings in the real league as matches are played,
**So that** I can track how the season is progressing.

**Acceptance Criteria:**
- Returns team positions, matches played, wins, draws, losses, and league points
- Updated automatically after each match result is published
- Available throughout the season (not just after lock)

---

## Notifications

### US-024 — Receive Prediction Reminder
**As a** user,
**I want to** receive an email reminder before a match prediction window closes,
**So that** I don't miss the chance to submit my prediction.

**Acceptance Criteria:**
- Email is sent only to users who have **not** submitted a prediction for that match
- Reminder is sent at a configurable time before the lock (e.g., 2 hours before)
- Email includes match details and the prediction deadline
- Email is logged to the `email_log` table with status

---

### US-025 — Receive Result Update Alert (Admin)
**As an** admin,
**I want to** receive an email alert when a match result is pending update,
**So that** I am reminded to publish the result promptly.

**Acceptance Criteria:**
- Alert is triggered after a match's `scheduled_at` time has passed and no result has been published
- Email includes the match details and a direct reference to the result endpoint
- Email is logged to `email_log`

---

### US-026 — Receive Leaderboard Recalculation Notification (Admin)
**As an** admin,
**I want to** receive an email when leaderboard recalculation completes after a result is published,
**So that** I know the scores are up to date.

**Acceptance Criteria:**
- Email sent to admin after async scoring + leaderboard rebuild finishes
- Includes season name, match, and a summary of users updated
- Email is logged to `email_log`

---

### US-027 — Bulk Communicate with Users (Admin)
**As an** admin,
**I want to** send a custom message to a selected group of users,
**So that** I can communicate announcements or event-specific information.

**Acceptance Criteria:**
- Admin selects recipient user IDs (or all users) and an event type
- Admin provides a custom message body
- System dispatches individual emails per recipient
- Every email is logged to `email_log` (regardless of success or failure)
- Failed emails have an error message stored in `email_log`

---

### US-028 — View Email Log (Admin)
**As an** admin,
**I want to** view the history of all emails sent by the platform,
**So that** I can audit notification delivery and investigate failures.

**Acceptance Criteria:**
- Paginated list of all `email_log` records
- Filterable by recipient, event type, status (SENT / FAILED / PENDING), and date range
- Sortable by sent timestamp

---

## System Behaviour (Implicit)

### US-029 — Automatic Match Prediction Lock
**As the** system,
**I need to** automatically lock match predictions at the configured time before each match,
**So that** no predictions are accepted after the cutoff without manual admin intervention.

**Acceptance Criteria:**
- A scheduled job runs and sets `match.status = LOCKED` at `lock_time`
- Any prediction submission after `lock_time` is rejected at both service and DB level
- Lock is enforced even if the scheduled job is delayed (DB constraint is the final guard)

---

### US-030 — Automatic League Prediction Lock
**As the** system,
**I need to** automatically lock league standing predictions 4 hours before the first match in the season,
**So that** users cannot change their league predictions once the season begins.

**Acceptance Criteria:**
- `season.status` transitions from `OPEN` to `LOCKED` at `league_lock_time`
- Any league prediction submission after this time is rejected
- `league_lock_time` is derived from `first_match_scheduled_at - league_lock_offset`

---

### US-031 — Asynchronous Score Calculation
**As the** system,
**I need to** calculate user scores in the background after a result is published,
**So that** result publishing is fast and scoring does not block the admin's response.

**Acceptance Criteria:**
- Admin's publish result API returns immediately after saving the result
- Scoring job runs asynchronously and processes all predictions for the match
- Points are written only by the scoring service — never by any API input
- Admin is notified by email once scoring and leaderboard update complete
- If scoring fails, the error is logged and admin is notified

---

## Configuration (Admin)

### US-032 — View and Update App Configuration
**As an** admin,
**I want to** view and update platform configuration values (lock offsets, reminder timings, etc.),
**So that** I can tune the platform behaviour without a code deployment.

**Acceptance Criteria:**
- Returns all `app_config` key-value pairs
- Admin can update individual config values via API
- Updated values take effect on next read (cache evicts on update)
- Config keys are validated — unknown keys are rejected
