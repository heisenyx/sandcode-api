CREATE TABLE jobs
(
    id          UUID                        NOT NULL,
    lang        VARCHAR(255)                NOT NULL,
    status      VARCHAR(255)                NOT NULL,
    code        OID                         NOT NULL,
    stdin       OID,
    stdout      OID,
    exit_code   INTEGER,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_jobs PRIMARY KEY (id)
);