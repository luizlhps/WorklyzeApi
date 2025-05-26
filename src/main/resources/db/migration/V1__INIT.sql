
CREATE SEQUENCE IF NOT EXISTS type_provider_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS type_status_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE activity
(
    id         UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    start_time TIMESTAMP WITHOUT TIME ZONE,
    end_time   TIMESTAMP WITHOUT TIME ZONE,
    name       VARCHAR(255),
    task_id    UUID,
    user_id    UUID,
    CONSTRAINT pk_activity PRIMARY KEY (id)
);

CREATE TABLE address
(
    id         UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    city       VARCHAR(255),
    street     VARCHAR(255),
    state      VARCHAR(255),
    zip        VARCHAR(255),
    user_id    UUID,
    CONSTRAINT pk_address PRIMARY KEY (id)
);

CREATE TABLE demand
(
    id         UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    name       VARCHAR(255),
    start_date date,
    end_date   date,
    total_time VARCHAR(255),
    status_id  BIGINT,
    user_id    UUID,
    CONSTRAINT pk_demand PRIMARY KEY (id)
);

CREATE TABLE task
(
    id          UUID NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    nome        VARCHAR(255),
    tempo_total VARCHAR(255),
    rest_total  VARCHAR(255),
    demand_id   UUID,
    is_deleted  BOOLEAN,
    status_id   BIGINT,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE type_provider
(
    id         BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    nome       VARCHAR(255),
    CONSTRAINT pk_typeprovider PRIMARY KEY (id)
);

CREATE TABLE type_status
(
    id         BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    nome       VARCHAR(255),
    CONSTRAINT pk_typestatus PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id                        UUID NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    name                      VARCHAR(255),
    username                  VARCHAR(255),
    email                     VARCHAR(255),
    password                  VARCHAR(255),
    recovery_token            VARCHAR(255),
    recovery_expiry_time      TIMESTAMP WITHOUT TIME ZONE,
    refresh_token             VARCHAR(255),
    refresh_token_expiry_time TIMESTAMP WITHOUT TIME ZONE,
    provider_id               BIGINT,
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


/* register provider*/
insert into type_provider (id, nome) values (1, 'GOOGLE');
insert into type_provider (id, nome) values (2, 'LOCAL');

/*register status*/
insert into type_status (id, nome) values (1, 'ABERTO');
insert into type_status (id, nome) values (2, 'FECHADO');
insert into type_status (id, nome) values (3, 'DENSEVOLVIMENTO');
insert into type_status (id, nome) values (4, 'TESTES');