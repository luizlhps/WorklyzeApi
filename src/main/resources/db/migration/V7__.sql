ALTER TABLE activity
    ADD is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE activity
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE address
    ADD is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE address
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE demand
    ADD is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE demand
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE type_provider
    ADD is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE type_provider
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE type_status
    ADD is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE type_status
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE "user"
    ADD is_deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE "user"
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE task
    ADD time_total BIGINT;

ALTER TABLE task
    ALTER COLUMN time_total SET NOT NULL;

ALTER TABLE activity
    DROP COLUMN deleted;

ALTER TABLE address
    DROP COLUMN deleted;

ALTER TABLE demand
    DROP COLUMN deleted;

ALTER TABLE task
    DROP COLUMN deleted;

ALTER TABLE task
    DROP COLUMN tempo_total;

ALTER TABLE type_provider
    DROP COLUMN deleted;

ALTER TABLE type_status
    DROP COLUMN deleted;

ALTER TABLE "user"
    DROP COLUMN deleted;

ALTER TABLE demand
    ALTER COLUMN end_date DROP NOT NULL;