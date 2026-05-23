CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255),
    display_name    VARCHAR(255) NOT NULL,
    avatar_url      VARCHAR(500),
    role            VARCHAR(50)  NOT NULL DEFAULT 'USER'
                        CHECK (role IN ('ADMIN', 'USER')),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,

    -- audit fields
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_at      TIMESTAMP WITH TIME ZONE,
    updated_by      UUID,
    deleted_at      TIMESTAMP WITH TIME ZONE,
    deleted_by      UUID,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_users_email     ON users (email);
CREATE INDEX idx_users_is_deleted ON users (is_deleted);
