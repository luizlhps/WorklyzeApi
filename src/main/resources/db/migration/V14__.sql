CREATE TABLE annotation
(
    id         UUID                                    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    is_deleted BOOLEAN DEFAULT FALSE                   NOT NULL,
    name       TEXT                                    NOT NULL,
    text_block TEXT                                    NOT NULL,
    CONSTRAINT pk_annotation PRIMARY KEY (id)
);