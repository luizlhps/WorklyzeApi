CREATE TABLE activity
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted    BOOLEAN,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    task_id    UUID,
    user_id    UUID,
    CONSTRAINT pk_activity PRIMARY KEY (id)
);

CREATE TABLE address
(
    id         UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted    BOOLEAN,
    city       VARCHAR(255) NOT NULL,
    street     VARCHAR(255) NOT NULL,
    state      VARCHAR(255) NOT NULL,
    zip        VARCHAR(255) NOT NULL,
    user_id    UUID         NOT NULL,
    CONSTRAINT pk_address PRIMARY KEY (id)
);

CREATE TABLE demand
(
    id         UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted    BOOLEAN,
    name       VARCHAR(255) NOT NULL,
    start_date date         NOT NULL,
    end_date   date         NOT NULL,
    total_time BIGINT       NOT NULL,
    status_id  BIGINT       NOT NULL,
    user_id    UUID         NOT NULL,
    CONSTRAINT pk_demand PRIMARY KEY (id)
);

CREATE TABLE task
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    deleted     BOOLEAN,
    nome        VARCHAR(255) NOT NULL,
    tempo_total BIGINT       NOT NULL,
    rest_total  BIGINT       NOT NULL,
    demand_id   UUID         NOT NULL,
    is_deleted  BOOLEAN      NOT NULL,
    status_id   BIGINT       NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE type_provider
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted    BOOLEAN,
    nome       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_typeprovider PRIMARY KEY (id)
);

CREATE TABLE type_status
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    deleted    BOOLEAN,
    nome       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_typestatus PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id                        UUID                        NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    deleted                   BOOLEAN,
    name                      VARCHAR(255)                NOT NULL,
    surname                   VARCHAR(255)                NOT NULL,
    username                  VARCHAR(255),
    email                     VARCHAR(255)                NOT NULL,
    password                  VARCHAR(255)                NOT NULL,
    recovery_token            VARCHAR(255),
    recovery_expiry_time      TIMESTAMP WITHOUT TIME ZONE,
    refresh_token             VARCHAR(255)                NOT NULL,
    refresh_token_expiry_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    provider_id               BIGINT                      NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE activity
    ADD CONSTRAINT FK_ACTIVITY_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE activity
    ADD CONSTRAINT FK_ACTIVITY_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE address
    ADD CONSTRAINT FK_ADDRESS_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE demand
    ADD CONSTRAINT FK_DEMAND_ON_STATUS FOREIGN KEY (status_id) REFERENCES type_status (id);

ALTER TABLE demand
    ADD CONSTRAINT FK_DEMAND_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_DEMAND FOREIGN KEY (demand_id) REFERENCES demand (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_STATUS FOREIGN KEY (status_id) REFERENCES type_status (id);

ALTER TABLE "user"
    ADD CONSTRAINT FK_USER_ON_PROVIDER FOREIGN KEY (provider_id) REFERENCES type_provider (id);