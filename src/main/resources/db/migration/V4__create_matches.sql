CREATE TABLE matches (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id       UUID        NOT NULL REFERENCES seasons (id),
    home_team_id    UUID        NOT NULL REFERENCES teams (id),
    away_team_id    UUID        NOT NULL REFERENCES teams (id),
    scheduled_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    lock_time       TIMESTAMP WITH TIME ZONE NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED'
                        CHECK (status IN ('SCHEDULED', 'LOCKED', 'COMPLETED')),

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_teams_differ CHECK (home_team_id <> away_team_id)
);

CREATE INDEX idx_matches_season_id    ON matches (season_id);
CREATE INDEX idx_matches_scheduled_at ON matches (scheduled_at);
CREATE INDEX idx_matches_lock_time    ON matches (lock_time);
CREATE INDEX idx_matches_status       ON matches (status);

CREATE TABLE match_results (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id                UUID NOT NULL UNIQUE REFERENCES matches (id),
    winner_team_id          UUID REFERENCES teams (id),
    toss_winner_team_id     UUID REFERENCES teams (id),
    player_of_match_id      UUID REFERENCES players (id),
    is_draw                 BOOLEAN NOT NULL DEFAULT FALSE,
    published_at            TIMESTAMP WITH TIME ZONE,
    published_by            UUID,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);
