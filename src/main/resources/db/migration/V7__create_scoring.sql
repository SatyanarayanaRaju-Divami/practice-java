-- Aggregated user points per season. Written only by the scoring engine — never via API.
CREATE TABLE scores (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id       UUID NOT NULL REFERENCES seasons (id),
    user_id         UUID NOT NULL REFERENCES users (id),
    total_points    INT  NOT NULL DEFAULT 0,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_scores UNIQUE (season_id, user_id)
);

-- Per-prediction breakdown of how points were earned
CREATE TABLE score_breakdown (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score_id            UUID        NOT NULL REFERENCES scores (id),
    match_id            UUID        REFERENCES matches (id),
    prediction_type     VARCHAR(50) NOT NULL
                            CHECK (prediction_type IN ('MATCH_WINNER', 'TOSS', 'POTM', 'LEAGUE_STANDING')),
    points_earned       INT         NOT NULL,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_score_breakdown_score_id ON score_breakdown (score_id);

-- Pre-computed user prediction rankings per season. Rebuilt after each scoring run.
CREATE TABLE leaderboard (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id           UUID NOT NULL REFERENCES seasons (id),
    user_id             UUID NOT NULL REFERENCES users (id),
    rank                INT  NOT NULL,
    total_points        INT  NOT NULL DEFAULT 0,
    last_calculated_at  TIMESTAMP WITH TIME ZONE,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_leaderboard UNIQUE (season_id, user_id)
);

CREATE INDEX idx_leaderboard_season_rank ON leaderboard (season_id, rank);
