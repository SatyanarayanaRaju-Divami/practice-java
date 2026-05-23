-- User predictions for full league standings (positions 1..n for all teams)
CREATE TABLE predictions_league (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id           UUID NOT NULL REFERENCES seasons (id),
    user_id             UUID NOT NULL REFERENCES users (id),
    team_id             UUID NOT NULL REFERENCES teams (id),
    predicted_position  INT  NOT NULL,
    submitted_at        TIMESTAMP WITH TIME ZONE NOT NULL,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_predictions_league UNIQUE (season_id, user_id, team_id)
);

CREATE INDEX idx_pred_league_season_user ON predictions_league (season_id, user_id);

-- User predictions per match (winner, toss, player of the match)
CREATE TABLE predictions_match (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id                    UUID NOT NULL REFERENCES matches (id),
    user_id                     UUID NOT NULL REFERENCES users (id),
    predicted_winner_team_id    UUID REFERENCES teams (id),
    predicted_toss_winner_id    UUID REFERENCES teams (id),
    predicted_potm_player_id    UUID REFERENCES players (id),
    submitted_at                TIMESTAMP WITH TIME ZONE NOT NULL,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_predictions_match UNIQUE (match_id, user_id)
);

CREATE INDEX idx_pred_match_match_user ON predictions_match (match_id, user_id);
