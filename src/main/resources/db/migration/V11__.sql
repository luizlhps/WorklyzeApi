ALTER TABLE task
    ADD name VARCHAR(255);

UPDATE task t SET name = t.nome;

ALTER TABLE task
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE task
    DROP COLUMN nome;

ALTER TABLE activity
    ALTER COLUMN end_time DROP NOT NULL;