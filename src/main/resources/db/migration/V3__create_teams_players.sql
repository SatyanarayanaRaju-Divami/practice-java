CREATE TABLE teams (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    logo_url    VARCHAR(500),

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE season_teams (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id       UUID NOT NULL REFERENCES seasons (id),
    team_id         UUID NOT NULL REFERENCES teams (id),
    seed_position   INT,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_season_teams UNIQUE (season_id, team_id)
);

CREATE TABLE players (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id         UUID         NOT NULL REFERENCES teams (id),
    name            VARCHAR(255) NOT NULL,
    jersey_number   INT,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_players_team_id ON players (team_id);
