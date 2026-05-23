-- Tracks actual real-world team positions in the season.
-- Distinct from the user prediction leaderboard (see V7).
CREATE TABLE league_standings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id           UUID NOT NULL REFERENCES seasons (id),
    team_id             UUID NOT NULL REFERENCES teams (id),
    current_position    INT,
    matches_played      INT NOT NULL DEFAULT 0,
    wins                INT NOT NULL DEFAULT 0,
    draws               INT NOT NULL DEFAULT 0,
    losses              INT NOT NULL DEFAULT 0,
    points_in_league    INT NOT NULL DEFAULT 0,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_league_standings UNIQUE (season_id, team_id)
);

CREATE INDEX idx_standings_season_id ON league_standings (season_id);
