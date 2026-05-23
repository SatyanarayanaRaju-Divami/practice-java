ALTER TABLE players ADD COLUMN team_name VARCHAR(255);

UPDATE players p
SET team_name = t.name
FROM teams t
WHERE p.team_id = t.id;

ALTER TABLE players ALTER COLUMN team_name SET NOT NULL;
