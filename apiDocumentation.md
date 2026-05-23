# Family League API Documentation

Backend API for a sports prediction platform where users predict match winners, toss results, and player of the match. Points are awarded for correct predictions and a leaderboard tracks rankings.

---

## Base URL

```
http://localhost:8080/api/v1
```

## Interactive Docs (Swagger UI)

```
http://localhost:8080/swagger-ui.html
```

---

## Authentication

All endpoints **except** `POST /auth/register` and `POST /auth/login` require a Bearer JWT token.

```
Authorization: Bearer <token>
```

Obtain a token via `POST /api/v1/auth/login`.

---

## Response Envelope

Every response is wrapped in a consistent envelope.

**Success**
```json
{
  "success": true,
  "message": "optional message",
  "data": { ... },
  "path": "/api/v1/..."
}
```

**Error**
```json
{
  "success": false,
  "message": "error description",
  "errors": [
    { "field": "email", "error": "must not be blank" }
  ],
  "path": "/api/v1/..."
}
```

---

## Pagination

Paginated endpoints accept these query parameters:

| Parameter | Default | Description |
|---|---|---|
| `page` | `0` | Zero-based page number |
| `size` | `20` | Page size |
| `sort` | varies | Field to sort by (e.g. `sort=name,asc`) |

Paginated responses include a `data` object with:

```json
{
  "content": [ ... ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20,
  "first": true,
  "last": false
}
```

---

## HTTP Status Codes

| Code | Meaning |
|---|---|
| `200` | OK |
| `201` | Created |
| `400` | Bad request / validation error |
| `401` | Unauthorized (missing or invalid token) |
| `403` | Forbidden (prediction window closed, insufficient role) |
| `404` | Resource not found |
| `409` | Conflict (duplicate email, data integrity violation) |
| `422` | Business rule violation (season not activatable, etc.) |

---

## Admin Workflow Order

```
Create League → Create Season → Enroll Teams → Schedule Matches
→ Activate Season → Users Submit Predictions → Publish Match Results
→ Publish Final Standings → Close Season
```

---

---

# Authentication

## POST /auth/register

Register a new user account.

**Auth:** None

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "secret123",
  "displayName": "John Doe"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `email` | string | yes | Must be unique |
| `password` | string | yes | |
| `displayName` | string | yes | |

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "a1b2c3d4-...",
    "role": "USER"
  },
  "path": "/api/v1/auth/register"
}
```

---

## POST /auth/login

Login and obtain a JWT token.

**Auth:** None

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "a1b2c3d4-...",
    "role": "USER"
  },
  "path": "/api/v1/auth/login"
}
```

---

## POST /auth/refresh

Refresh an existing JWT token.

**Auth:** Bearer token (can be near-expiry)

**Request Header**
```
Authorization: Bearer <current-token>
```

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "a1b2c3d4-...",
    "role": "USER"
  },
  "path": "/api/v1/auth/refresh"
}
```

---

## POST /auth/admin

Create an admin account. Requires an existing admin token.

**Auth:** Bearer token (ADMIN)

**Request Body**
```json
{
  "email": "admin@example.com",
  "password": "adminpass",
  "displayName": "Admin User"
}
```

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "b2c3d4e5-...",
    "role": "ADMIN"
  },
  "path": "/api/v1/auth/admin"
}
```

---

---

# Leagues

## GET /leagues

List all leagues (paginated).

**Auth:** Bearer token  
**Query Params:** `page`, `size`, `sort` (default: `sort=name`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "a1b2c3d4-...",
        "name": "Premier League",
        "description": "Top tier league",
        "createdAt": "2026-01-01T10:00:00Z"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/leagues"
}
```

---

## GET /leagues/{id}

Get a single league by ID.

**Auth:** Bearer token

**Path Params:** `id` (UUID)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "id": "a1b2c3d4-...",
    "name": "Premier League",
    "description": "Top tier league",
    "createdAt": "2026-01-01T10:00:00Z"
  },
  "path": "/api/v1/leagues/a1b2c3d4-..."
}
```

---

## POST /leagues

Create a new league.

**Auth:** Bearer token (ADMIN)

**Request Body**
```json
{
  "name": "Premier League",
  "description": "Top tier league"
}
```

| Field | Type | Required |
|---|---|---|
| `name` | string | yes |
| `description` | string | no |

**Response 201**
```json
{
  "success": true,
  "message": "League created successfully",
  "data": {
    "id": "a1b2c3d4-...",
    "name": "Premier League",
    "description": "Top tier league",
    "createdAt": "2026-01-01T10:00:00Z"
  },
  "path": "/api/v1/leagues"
}
```

---

## PUT /leagues/{id}

Update a league.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Request Body**
```json
{
  "name": "Updated League Name",
  "description": "Updated description"
}
```

**Response 200** — same as GET /leagues/{id}

---

## DELETE /leagues/{id}

Soft-delete a league.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200**
```json
{
  "success": true,
  "message": "Deleted successfully",
  "data": null,
  "path": "/api/v1/leagues/a1b2c3d4-..."
}
```

---

---

# Seasons

## GET /leagues/{leagueId}/seasons

List seasons for a league (paginated).

**Auth:** Bearer token  
**Path Params:** `leagueId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=name`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "s1s2s3s4-...",
        "leagueId": "a1b2c3d4-...",
        "leagueName": "Premier League",
        "name": "Season 2026",
        "status": "DRAFT",
        "firstMatchStartTime": null,
        "leagueLockTime": null,
        "createdAt": "2026-01-01T10:00:00Z"
      }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/leagues/a1b2c3d4-.../seasons"
}
```

**Season statuses:** `DRAFT` → `OPEN` → `LOCKED` → `COMPLETED` → `CLOSED`

---

## POST /leagues/{leagueId}/seasons

Create a new season under a league.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `leagueId` (UUID)

**Request Body**
```json
{
  "name": "Season 2026"
}
```

**Response 201** — single `SeasonResponse` wrapped in envelope

---

## GET /seasons/{id}

Get a season by ID.

**Auth:** Bearer token  
**Path Params:** `id` (UUID)

**Response 200** — single `SeasonResponse` wrapped in envelope

---

## PUT /seasons/{id}/activate

Activate a season: transitions status `DRAFT → OPEN`.  
Sets `leagueLockTime` based on the first match start time minus the configured lock offset.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)  
**Request Body:** none

**Preconditions (returns 422 if not met):**
- Season must be in `DRAFT` status
- At least one team must be enrolled
- At least one match must be scheduled
- First match start time must be set

**Response 200** — single `SeasonResponse` with `status: "OPEN"`

---

## PUT /seasons/{id}/close

Close a season: transitions status `COMPLETED → CLOSED`.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)  
**Request Body:** none

**Response 200** — single `SeasonResponse` with `status: "CLOSED"`

---

## DELETE /seasons/{id}

Soft-delete a season.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200** — deleted envelope

---

## GET /seasons/{id}/teams

List teams enrolled in a season.

**Auth:** Bearer token  
**Path Params:** `id` (UUID)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": [
    {
      "id": "e1e2e3e4-...",
      "seasonId": "s1s2s3s4-...",
      "teamId": "t1t2t3t4-...",
      "teamName": "Arsenal",
      "seedPosition": 1
    }
  ],
  "path": "/api/v1/seasons/s1s2s3s4-.../teams"
}
```

---

## POST /seasons/{id}/teams

Enroll a team in a season.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID — season ID)

**Request Body**
```json
{
  "teamId": "t1t2t3t4-...",
  "seedPosition": 1
}
```

| Field | Type | Required |
|---|---|---|
| `teamId` | UUID | yes |
| `seedPosition` | integer | no |

**Response 201** — single `SeasonTeamResponse` wrapped in envelope

---

## DELETE /seasons/{seasonId}/teams/{teamId}

Remove a team from a season.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `seasonId` (UUID), `teamId` (UUID)

**Response 200** — deleted envelope

---

## POST /seasons/{id}/publish-result

Publish final league standings and trigger league score calculation. Transitions season to `COMPLETED`.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID — season ID)

**Request Body**
```json
{
  "standings": [
    { "teamId": "t1t2t3t4-...", "finalPosition": 1 },
    { "teamId": "t2t3t4t5-...", "finalPosition": 2 }
  ]
}
```

**Response 200**
```json
{
  "success": true,
  "message": "Final standings published",
  "data": null,
  "path": "/api/v1/seasons/s1s2s3s4-.../publish-result"
}
```

---

---

# Teams

## GET /teams

List all teams (paginated).

**Auth:** Bearer token  
**Query Params:** `page`, `size`, `sort` (default: `sort=name`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "t1t2t3t4-...",
        "name": "Arsenal",
        "logoUrl": "https://example.com/arsenal.png",
        "createdAt": "2026-01-01T10:00:00Z"
      }
    ],
    "totalElements": 20,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/teams"
}
```

---

## GET /teams/{id}

Get a team by ID.

**Auth:** Bearer token  
**Path Params:** `id` (UUID)

**Response 200** — single `TeamResponse` wrapped in envelope

---

## POST /teams

Create a new team.

**Auth:** Bearer token (ADMIN)

**Request Body**
```json
{
  "name": "Arsenal",
  "logoUrl": "https://example.com/arsenal.png"
}
```

| Field | Type | Required |
|---|---|---|
| `name` | string | yes |
| `logoUrl` | string | no |

**Response 201** — single `TeamResponse` wrapped in envelope

---

## PUT /teams/{id}

Update a team.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Request Body** — same as POST /teams

**Response 200** — single `TeamResponse` wrapped in envelope

---

## DELETE /teams/{id}

Soft-delete a team.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200** — deleted envelope

---

---

# Players

## GET /players

List all players (paginated).

**Auth:** Bearer token  
**Query Params:** `page`, `size`, `sort` (default: `sort=name`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "p1p2p3p4-...",
        "teamId": "t1t2t3t4-...",
        "teamName": "Arsenal",
        "name": "Bukayo Saka",
        "jerseyNumber": 7
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/players"
}
```

---

## GET /players/by-team/{teamId}

List players filtered by team (paginated).

**Auth:** Bearer token  
**Path Params:** `teamId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=name`)

**Response 200** — paginated `PlayerResponse` list

---

## GET /players/{id}

Get a player by ID.

**Auth:** Bearer token  
**Path Params:** `id` (UUID)

**Response 200** — single `PlayerResponse` wrapped in envelope

---

## POST /players

Create a new player.

**Auth:** Bearer token (ADMIN)

**Request Body**
```json
{
  "teamId": "t1t2t3t4-...",
  "name": "Bukayo Saka",
  "jerseyNumber": 7
}
```

| Field | Type | Required |
|---|---|---|
| `teamId` | UUID | yes |
| `name` | string | yes |
| `jerseyNumber` | integer | no |

**Response 201** — single `PlayerResponse` wrapped in envelope

---

## PUT /players/{id}

Update a player.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Request Body**
```json
{
  "name": "Bukayo Saka",
  "jerseyNumber": 7,
  "teamId": "t1t2t3t4-..."
}
```

**Response 200** — single `PlayerResponse` wrapped in envelope

---

## DELETE /players/{id}

Delete a player.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200** — deleted envelope

---

---

# Matches

## GET /seasons/{seasonId}/matches

List matches for a season (paginated).

**Auth:** Bearer token  
**Path Params:** `seasonId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=scheduledAt`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "m1m2m3m4-...",
        "seasonId": "s1s2s3s4-...",
        "homeTeamId": "t1t2t3t4-...",
        "homeTeamName": "Arsenal",
        "awayTeamId": "t2t3t4t5-...",
        "awayTeamName": "Chelsea",
        "scheduledAt": "2026-06-10T15:00:00Z",
        "lockTime": "2026-06-10T13:00:00Z",
        "status": "SCHEDULED"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/seasons/s1s2s3s4-.../matches"
}
```

**Match statuses:** `SCHEDULED` → `LOCKED` → `COMPLETED`

---

## POST /seasons/{seasonId}/matches

Schedule a new match.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `seasonId` (UUID)

**Request Body**
```json
{
  "homeTeamId": "t1t2t3t4-...",
  "awayTeamId": "t2t3t4t5-...",
  "scheduledAt": "2026-06-10T15:00:00Z"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `homeTeamId` | UUID | yes | Must be enrolled in the season |
| `awayTeamId` | UUID | yes | Must be enrolled; cannot equal `homeTeamId` |
| `scheduledAt` | ISO-8601 datetime | yes | |

**Response 201** — single `MatchResponse` wrapped in envelope

---

## GET /matches/{id}

Get a match by ID.

**Auth:** Bearer token  
**Path Params:** `id` (UUID)

**Response 200** — single `MatchResponse` wrapped in envelope

---

## PUT /matches/{id}

Update match schedule. Only allowed while status is `SCHEDULED`.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Request Body**
```json
{
  "scheduledAt": "2026-06-11T15:00:00Z"
}
```

**Response 200** — single `MatchResponse` wrapped in envelope

---

## DELETE /matches/{id}

Soft-delete a match.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200** — deleted envelope

---

## POST /matches/{id}/result

Publish a match result. Triggers async score calculation.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID — match ID)

**Request Body**
```json
{
  "winnerTeamId": "t1t2t3t4-...",
  "tossWinnerTeamId": "t2t3t4t5-...",
  "playerOfMatchId": "p1p2p3p4-...",
  "isDraw": false
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `isDraw` | boolean | yes | If `true`, `winnerTeamId` is ignored |
| `winnerTeamId` | UUID | no | Required when `isDraw` is `false` |
| `tossWinnerTeamId` | UUID | yes | Must be one of the two match teams |
| `playerOfMatchId` | UUID | yes | Must belong to one of the two match teams |

**Response 201**
```json
{
  "success": true,
  "message": "Match result published",
  "data": {
    "id": "r1r2r3r4-...",
    "matchId": "m1m2m3m4-...",
    "winnerTeamId": "t1t2t3t4-...",
    "winnerTeamName": "Arsenal",
    "tossWinnerTeamId": "t2t3t4t5-...",
    "tossWinnerTeamName": "Chelsea",
    "playerOfMatchId": "p1p2p3p4-...",
    "playerOfMatchName": "Bukayo Saka",
    "isDraw": false,
    "publishedAt": "2026-06-10T17:30:00Z"
  },
  "path": "/api/v1/matches/m1m2m3m4-.../result"
}
```

---

## GET /matches/{id}/result

Get the published result for a match.

**Auth:** Bearer token  
**Path Params:** `id` (UUID)

**Response 200** — single `MatchResultResponse` wrapped in envelope (see POST /matches/{id}/result for shape)

---

---

# Predictions

## POST /matches/{matchId}/predictions

Submit or update a match prediction. Only allowed while match status is `SCHEDULED` and before `lockTime`.

**Auth:** Bearer token (USER)  
**Path Params:** `matchId` (UUID)

**Request Body**
```json
{
  "predictedWinnerTeamId": "t1t2t3t4-...",
  "predictedTossWinnerId": "t2t3t4t5-...",
  "predictedPotmPlayerId": "p1p2p3p4-..."
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `predictedWinnerTeamId` | UUID | no | `null` = predicting a draw |
| `predictedTossWinnerId` | UUID | no | Must be one of the match teams |
| `predictedPotmPlayerId` | UUID | no | Must belong to one of the match teams |

**Response 200**
```json
{
  "success": true,
  "message": "Match prediction submitted",
  "data": {
    "matchId": "m1m2m3m4-...",
    "userId": "u1u2u3u4-...",
    "userDisplayName": "John Doe",
    "predictedWinnerTeamId": "t1t2t3t4-...",
    "predictedWinnerTeamName": "Arsenal",
    "predictedTossWinnerId": "t2t3t4t5-...",
    "predictedTossWinnerName": "Chelsea",
    "predictedPotmPlayerId": "p1p2p3p4-...",
    "predictedPotmPlayerName": "Bukayo Saka",
    "submittedAt": "2026-06-10T12:00:00Z",
    "winnerCorrect": null,
    "tossCorrect": null,
    "potmCorrect": null
  },
  "path": "/api/v1/matches/m1m2m3m4-.../predictions"
}
```

> `winnerCorrect`, `tossCorrect`, `potmCorrect` are `null` until the result is published, then `true`/`false`.

**Error 403** — prediction window is closed (match locked or status not SCHEDULED)

---

## GET /matches/{matchId}/predictions/me

Get the current user's match prediction.

**Auth:** Bearer token  
**Path Params:** `matchId` (UUID)

**Response 200** — single `MatchPredictionResponse` or `null` data if not yet submitted

---

## GET /matches/{matchId}/predictions

Get all predictions for a match (paginated). Only accessible after the prediction window closes (match is LOCKED or COMPLETED).

**Auth:** Bearer token  
**Path Params:** `matchId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=submittedAt`)

**Response 200** — paginated `MatchPredictionResponse` list, with `winnerCorrect`/`tossCorrect`/`potmCorrect` populated if result is published

**Error 403** — prediction window is still open

---

## GET /matches/{matchId}/predictions/head-to-head

Compare your prediction with another user's prediction for the same match. Only accessible after the prediction window closes.

**Auth:** Bearer token  
**Path Params:** `matchId` (UUID)  
**Query Params:** `opponentId` (UUID, required)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "myPrediction": {
      "matchId": "m1m2m3m4-...",
      "userId": "u1u2u3u4-...",
      "userDisplayName": "John Doe",
      "predictedWinnerTeamId": "t1t2t3t4-...",
      "predictedWinnerTeamName": "Arsenal",
      "predictedTossWinnerId": "t2t3t4t5-...",
      "predictedTossWinnerName": "Chelsea",
      "predictedPotmPlayerId": "p1p2p3p4-...",
      "predictedPotmPlayerName": "Bukayo Saka",
      "submittedAt": "2026-06-10T12:00:00Z",
      "winnerCorrect": true,
      "tossCorrect": false,
      "potmCorrect": true
    },
    "theirPrediction": {
      "matchId": "m1m2m3m4-...",
      "userId": "u2u3u4u5-...",
      "userDisplayName": "Jane Smith",
      "predictedWinnerTeamId": "t2t3t4t5-...",
      "predictedWinnerTeamName": "Chelsea",
      "predictedTossWinnerId": "t1t2t3t4-...",
      "predictedTossWinnerName": "Arsenal",
      "predictedPotmPlayerId": "p2p3p4p5-...",
      "predictedPotmPlayerName": "Cole Palmer",
      "submittedAt": "2026-06-10T11:00:00Z",
      "winnerCorrect": false,
      "tossCorrect": true,
      "potmCorrect": false
    }
  },
  "path": "/api/v1/matches/m1m2m3m4-.../predictions/head-to-head"
}
```

> `myPrediction` or `theirPrediction` will be `null` if the respective user has not submitted a prediction.

---

## POST /seasons/{seasonId}/predictions/league

Submit or replace the current user's full-season league position prediction. Only allowed before `leagueLockTime`.

**Auth:** Bearer token (USER)  
**Path Params:** `seasonId` (UUID)

**Request Body**
```json
{
  "predictions": [
    { "teamId": "t1t2t3t4-...", "predictedPosition": 1 },
    { "teamId": "t2t3t4t5-...", "predictedPosition": 2 },
    { "teamId": "t3t4t5t6-...", "predictedPosition": 3 }
  ]
}
```

**Validation rules:**
- Must include **all** enrolled teams (no more, no less)
- Positions must be unique integers in the range `1..N` (N = number of enrolled teams)

**Response 200**
```json
{
  "success": true,
  "message": "League prediction submitted",
  "data": [
    {
      "seasonId": "s1s2s3s4-...",
      "userId": "u1u2u3u4-...",
      "userDisplayName": "John Doe",
      "teamId": "t1t2t3t4-...",
      "teamName": "Arsenal",
      "predictedPosition": 1,
      "submittedAt": "2026-05-01T10:00:00Z"
    }
  ],
  "path": "/api/v1/seasons/s1s2s3s4-.../predictions/league"
}
```

**Error 403** — league prediction window is closed (`leagueLockTime` has passed)  
**Error 400** — missing teams, out-of-range positions, or duplicate positions

---

## GET /seasons/{seasonId}/predictions/league

Get all league predictions for a season (paginated). Only accessible after `leagueLockTime` has passed.

**Auth:** Bearer token  
**Path Params:** `seasonId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=predictedPosition`)

**Response 200** — paginated `LeaguePredictionResponse` list

**Error 403** — league prediction window is still open

---

---

# Leaderboard

## GET /seasons/{seasonId}/leaderboard

Get the season leaderboard sorted by rank (paginated).

**Auth:** Bearer token  
**Path Params:** `seasonId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=rank`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "rank": 1,
        "userId": "u1u2u3u4-...",
        "displayName": "John Doe",
        "totalPoints": 15,
        "lastCalculatedAt": "2026-06-10T18:00:00Z"
      },
      {
        "rank": 2,
        "userId": "u2u3u4u5-...",
        "displayName": "Jane Smith",
        "totalPoints": 12,
        "lastCalculatedAt": "2026-06-10T18:00:00Z"
      }
    ],
    "totalElements": 30,
    "totalPages": 2,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/seasons/s1s2s3s4-.../leaderboard"
}
```

---

## GET /seasons/{seasonId}/leaderboard/me

Get the current user's rank and full score breakdown for a season.

**Auth:** Bearer token  
**Path Params:** `seasonId` (UUID)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "userId": "u1u2u3u4-...",
    "displayName": "John Doe",
    "rank": 1,
    "totalPoints": 15,
    "lastCalculatedAt": "2026-06-10T18:00:00Z",
    "breakdown": [
      {
        "matchId": "m1m2m3m4-...",
        "predictionType": "MATCH_WINNER",
        "pointsEarned": 1
      },
      {
        "matchId": "m1m2m3m4-...",
        "predictionType": "TOSS",
        "pointsEarned": 1
      },
      {
        "matchId": "m1m2m3m4-...",
        "predictionType": "POTM",
        "pointsEarned": 1
      },
      {
        "matchId": null,
        "predictionType": "LEAGUE_STANDING",
        "pointsEarned": 1
      }
    ]
  },
  "path": "/api/v1/seasons/s1s2s3s4-.../leaderboard/me"
}
```

**Prediction types:** `MATCH_WINNER`, `TOSS`, `POTM`, `LEAGUE_STANDING`

---

---

# League Standings

## GET /seasons/{seasonId}/standings

Get the league table (win/draw/loss standings) for a season (paginated).

**Auth:** Bearer token  
**Path Params:** `seasonId` (UUID)  
**Query Params:** `page`, `size`, `sort` (default: `sort=currentPosition`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "ls1ls2ls3-...",
        "seasonId": "s1s2s3s4-...",
        "teamId": "t1t2t3t4-...",
        "teamName": "Arsenal",
        "currentPosition": 1,
        "matchesPlayed": 10,
        "wins": 7,
        "draws": 2,
        "losses": 1,
        "pointsInLeague": 23
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/seasons/s1s2s3s4-.../standings"
}
```

---

---

# Users

## GET /users

List all users (paginated).

**Auth:** Bearer token (ADMIN)  
**Query Params:** `page`, `size`, `sort` (default: `sort=displayName`)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "u1u2u3u4-...",
        "displayName": "John Doe",
        "email": "john@example.com",
        "avatarUrl": null,
        "role": "USER",
        "createdAt": "2026-01-01T10:00:00Z"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/users"
}
```

---

## GET /users/me

Get the current user's profile.

**Auth:** Bearer token

**Response 200** — single `UserResponse` wrapped in envelope

---

## PUT /users/me

Update the current user's profile.

**Auth:** Bearer token

**Request Body**
```json
{
  "displayName": "John Updated",
  "avatarUrl": "https://example.com/avatar.png"
}
```

**Response 200** — single `UserResponse` wrapped in envelope

---

## GET /users/{id}

Get a user by ID.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200** — single `UserResponse` wrapped in envelope

---

## PUT /users/{id}

Update a user.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Request Body** — same as PUT /users/me

**Response 200** — single `UserResponse` wrapped in envelope

---

## DELETE /users/{id}

Soft-delete a user.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `id` (UUID)

**Response 200** — deleted envelope

---

---

# Notifications

## POST /notifications/bulk

Send a bulk notification email to selected users (or all active users if no IDs are specified).

**Auth:** Bearer token (ADMIN)

**Request Body**
```json
{
  "userIds": ["u1u2u3u4-...", "u2u3u4u5-..."],
  "eventType": "ANNOUNCEMENT",
  "subject": "Season starts tomorrow!",
  "body": "Don't forget to submit your predictions before midnight."
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `userIds` | UUID[] | no | If empty/null, sends to all active users |
| `eventType` | string | yes | Free-form event label |
| `subject` | string | yes | Email subject line |
| `body` | string | yes | Email body text |

**Response 200**
```json
{
  "success": true,
  "message": "Bulk notification queued for 42 recipient(s)",
  "data": null,
  "path": "/api/v1/notifications/bulk"
}
```

---

## GET /notifications/emails

Query email logs with optional filters (paginated).

**Auth:** Bearer token (ADMIN)  
**Query Params:**

| Param | Type | Description |
|---|---|---|
| `recipientEmail` | string | Filter by recipient email (partial match) |
| `eventType` | string | Filter by event type |
| `status` | enum | `SENT`, `FAILED`, `PENDING` |
| `from` | ISO-8601 | Filter logs sent after this time |
| `to` | ISO-8601 | Filter logs sent before this time |
| `page`, `size`, `sort` | | Default: `sort=sentAt` |

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "content": [
      {
        "id": "el1el2el3-...",
        "recipientEmail": "john@example.com",
        "eventType": "SCORE_UPDATE",
        "subject": "Your score has been updated",
        "status": "SENT",
        "sentAt": "2026-06-10T18:05:00Z",
        "errorMessage": null
      }
    ],
    "totalElements": 200,
    "totalPages": 10,
    "number": 0,
    "size": 20
  },
  "path": "/api/v1/notifications/emails"
}
```

---

---

# Configuration

## GET /config

List all application configuration key-value pairs.

**Auth:** Bearer token (ADMIN)

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": [
    {
      "key": "match.lock.offset.hours",
      "value": "2",
      "description": "Hours before match start to lock predictions"
    },
    {
      "key": "league.lock.offset.hours",
      "value": "24",
      "description": "Hours before first match to lock league predictions"
    }
  ],
  "path": "/api/v1/config"
}
```

---

## PUT /config/{key}

Update a configuration value.

**Auth:** Bearer token (ADMIN)  
**Path Params:** `key` (string, e.g. `match.lock.offset.hours`)

**Request Body**
```json
{
  "value": "3"
}
```

**Response 200**
```json
{
  "success": true,
  "message": "",
  "data": {
    "key": "match.lock.offset.hours",
    "value": "3",
    "description": "Hours before match start to lock predictions"
  },
  "path": "/api/v1/config/match.lock.offset.hours"
}
```

---

---

## Endpoint Summary

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /auth/register | None | Register new user |
| POST | /auth/login | None | Login |
| POST | /auth/refresh | Bearer | Refresh token |
| POST | /auth/admin | ADMIN | Create admin account |
| GET | /leagues | Bearer | List leagues |
| GET | /leagues/{id} | Bearer | Get league |
| POST | /leagues | ADMIN | Create league |
| PUT | /leagues/{id} | ADMIN | Update league |
| DELETE | /leagues/{id} | ADMIN | Delete league |
| GET | /leagues/{leagueId}/seasons | Bearer | List seasons |
| POST | /leagues/{leagueId}/seasons | ADMIN | Create season |
| GET | /seasons/{id} | Bearer | Get season |
| PUT | /seasons/{id}/activate | ADMIN | Activate season |
| PUT | /seasons/{id}/close | ADMIN | Close season |
| DELETE | /seasons/{id} | ADMIN | Delete season |
| GET | /seasons/{id}/teams | Bearer | List enrolled teams |
| POST | /seasons/{id}/teams | ADMIN | Enroll team |
| DELETE | /seasons/{seasonId}/teams/{teamId} | ADMIN | Remove team |
| POST | /seasons/{id}/publish-result | ADMIN | Publish final standings |
| GET | /teams | Bearer | List teams |
| GET | /teams/{id} | Bearer | Get team |
| POST | /teams | ADMIN | Create team |
| PUT | /teams/{id} | ADMIN | Update team |
| DELETE | /teams/{id} | ADMIN | Delete team |
| GET | /players | Bearer | List all players |
| GET | /players/by-team/{teamId} | Bearer | List players by team |
| GET | /players/{id} | Bearer | Get player |
| POST | /players | ADMIN | Create player |
| PUT | /players/{id} | ADMIN | Update player |
| DELETE | /players/{id} | ADMIN | Delete player |
| GET | /seasons/{seasonId}/matches | Bearer | List matches |
| POST | /seasons/{seasonId}/matches | ADMIN | Schedule match |
| GET | /matches/{id} | Bearer | Get match |
| PUT | /matches/{id} | ADMIN | Update match |
| DELETE | /matches/{id} | ADMIN | Delete match |
| POST | /matches/{id}/result | ADMIN | Publish result |
| GET | /matches/{id}/result | Bearer | Get result |
| POST | /matches/{matchId}/predictions | Bearer | Submit match prediction |
| GET | /matches/{matchId}/predictions/me | Bearer | Get my prediction |
| GET | /matches/{matchId}/predictions | Bearer | All predictions (after lock) |
| GET | /matches/{matchId}/predictions/head-to-head | Bearer | Head-to-head comparison |
| POST | /seasons/{seasonId}/predictions/league | Bearer | Submit league prediction |
| GET | /seasons/{seasonId}/predictions/league | Bearer | All league predictions (after lock) |
| GET | /seasons/{seasonId}/leaderboard | Bearer | Season leaderboard |
| GET | /seasons/{seasonId}/leaderboard/me | Bearer | My rank & breakdown |
| GET | /seasons/{seasonId}/standings | Bearer | League table |
| GET | /users | ADMIN | List users |
| GET | /users/me | Bearer | Get my profile |
| PUT | /users/me | Bearer | Update my profile |
| GET | /users/{id} | ADMIN | Get user |
| PUT | /users/{id} | ADMIN | Update user |
| DELETE | /users/{id} | ADMIN | Delete user |
| POST | /notifications/bulk | ADMIN | Send bulk notification |
| GET | /notifications/emails | ADMIN | Email logs |
| GET | /config | ADMIN | List config |
| PUT | /config/{key} | ADMIN | Update config value |
