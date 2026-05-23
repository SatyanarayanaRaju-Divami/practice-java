CREATE TABLE email_log (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_user_id   UUID REFERENCES users (id),
    recipient_email     VARCHAR(255) NOT NULL,
    event_type          VARCHAR(100) NOT NULL,
    subject             VARCHAR(500) NOT NULL,
    body_summary        TEXT,
    status              VARCHAR(50)  NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    sent_at             TIMESTAMP WITH TIME ZONE,
    error_message       TEXT,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_at  TIMESTAMP WITH TIME ZONE,
    updated_by  UUID,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    deleted_by  UUID,
    is_deleted  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_email_log_recipient    ON email_log (recipient_user_id);
CREATE INDEX idx_email_log_status       ON email_log (status);
CREATE INDEX idx_email_log_event_type   ON email_log (event_type);
