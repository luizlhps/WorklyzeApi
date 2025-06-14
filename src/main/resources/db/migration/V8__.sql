ALTER TABLE task
    ALTER COLUMN time_total TYPE interval USING time_total * INTERVAL '0 minutes';

ALTER TABLE task
    ALTER COLUMN rest_total TYPE interval USING rest_total * INTERVAL '0 minutes';

ALTER TABLE demand
    ALTER COLUMN total_time TYPE interval USING total_time * INTERVAL '0 minutes';