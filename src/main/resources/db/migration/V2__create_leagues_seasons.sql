CREATE TABLE leagues (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE seasons (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_id               UUID         NOT NULL REFERENCES leagues (id),
    name                    VARCHAR(255) NOT NULL,
    status                  VARCHAR(50)  NOT NULL DEFAULT 'DRAFT'
                                CHECK (status IN ('DRAFT', 'OPEN', 'LOCKED', 'COMPLETED', 'CLOSED')),
    first_match_start_time  TIMESTAMP WITH TIME ZONE,
    league_lock_time        TIMESTAMP WITH TIME ZONE,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_seasons_league_id  ON seasons (league_id);
CREATE INDEX idx_seasons_status     ON seasons (status);
