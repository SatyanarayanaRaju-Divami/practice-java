-- Guard: reject new/updated match predictions after the match lock_time
CREATE OR REPLACE FUNCTION check_match_prediction_lock()
RETURNS TRIGGER AS $$
DECLARE
    v_lock_time TIMESTAMPTZ;
BEGIN
    SELECT lock_time INTO v_lock_time FROM matches WHERE id = NEW.match_id;
    IF v_lock_time IS NOT NULL AND NOW() >= v_lock_time THEN
        RAISE EXCEPTION 'PREDICTION_LOCKED: match prediction window is closed';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_match_prediction_lock
BEFORE INSERT OR UPDATE ON predictions_match
FOR EACH ROW
WHEN (NEW.is_deleted = false)
EXECUTE FUNCTION check_match_prediction_lock();

-- Guard: reject new/updated league predictions after the season league_lock_time
CREATE OR REPLACE FUNCTION check_league_prediction_lock()
RETURNS TRIGGER AS $$
DECLARE
    v_lock_time TIMESTAMPTZ;
BEGIN
    SELECT league_lock_time INTO v_lock_time FROM seasons WHERE id = NEW.season_id;
    IF v_lock_time IS NOT NULL AND NOW() >= v_lock_time THEN
        RAISE EXCEPTION 'PREDICTION_LOCKED: league prediction window is closed';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_league_prediction_lock
BEFORE INSERT OR UPDATE ON predictions_league
FOR EACH ROW
WHEN (NEW.is_deleted = false)
EXECUTE FUNCTION check_league_prediction_lock();
