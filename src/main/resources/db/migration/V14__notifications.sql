-- Add reference_id to email_log for idempotency (stores match_id or season_id)
ALTER TABLE email_log ADD COLUMN reference_id UUID;
CREATE INDEX idx_email_log_reference ON email_log (event_type, reference_id) WHERE reference_id IS NOT NULL;

-- Add result pending alert config
INSERT INTO app_config (key, value, description) VALUES
    ('result.pending.alert.hours', '2', 'Hours after match scheduled_at before alerting admins about unpublished result')
ON CONFLICT (key) DO NOTHING;
