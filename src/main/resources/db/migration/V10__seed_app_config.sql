INSERT INTO app_config (key, value, description) VALUES
    ('match.lock.offset.hours',     '1',     'Hours before match start time to lock match predictions'),
    ('league.lock.offset.hours',    '4',     'Hours before first match start time to lock league predictions'),
    ('match.reminder.offset.hours', '2',     'Hours before match lock to send prediction reminder emails'),
    ('jwt.expiry.seconds',          '86400', 'JWT access token TTL in seconds (default: 24 hours)'),
    ('leaderboard.recalc.async',    'true',  'Whether leaderboard recalculation runs asynchronously after result publish');
