-- Runtime-tunable platform configuration. No audit fields — changes tracked via service-layer logging.
CREATE TABLE app_config (
    key         VARCHAR(255) PRIMARY KEY,
    value       VARCHAR(500) NOT NULL,
    description TEXT
);
