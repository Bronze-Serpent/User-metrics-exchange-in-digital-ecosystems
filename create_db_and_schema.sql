CREATE
    DATABASE metrics_exchange_db;

\c metrics_exchange_db;

CREATE SCHEMA metrics_exchange;

CREATE TABLE metrics_exchange.user
(
    id                BIGSERIAL PRIMARY KEY NOT NULL,
    password_hash     VARCHAR(300),
    email             VARCHAR(250),
    role              VARCHAR(250),
    linked_company_id BIGINT,
    version           INTEGER,
    created_at        TIMESTAMPTZ
);

CREATE TABLE metrics_exchange.company
(
    id                                    BIGSERIAL PRIMARY KEY NOT NULL,
    name                                  VARCHAR(250),
    description                           VARCHAR(1000),
    supp_user_profile_exchange            BOOLEAN               NOT NULL,
    user_profile_import_topic_name        VARCHAR(250),
    trigger_url_for_export_user_portfolio VARCHAR(250),
    owner_user_id                         BIGINT REFERENCES metrics_exchange.user (id),
    version                               INTEGER,
    created_at                            TIMESTAMPTZ
);

ALTER TABLE metrics_exchange.user
    ADD CONSTRAINT user_company_foreign_key
        FOREIGN KEY (linked_company_id) REFERENCES metrics_exchange.company (id);

CREATE TABLE metrics_exchange.alliance
(
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(250),
    description VARCHAR(1000),
    version     INTEGER,
    created_at  TIMESTAMPTZ
);

CREATE TABLE metrics_exchange.alliance_point
(
    id          BIGSERIAL PRIMARY KEY                            NOT NULL,
    alliance_id BIGINT REFERENCES metrics_exchange.alliance (id) NOT NULL,
    format      TEXT,
    status      VARCHAR(250),
    version     INTEGER,
    created_at  TIMESTAMPTZ
);

CREATE TABLE metrics_exchange.company_point
(
    id                BIGSERIAL PRIMARY KEY NOT NULL,
    company_id        BIGINT                NOT NULL,
    format            TEXT,
    url               TEXT,
    status            VARCHAR(250),
    alliance_point_id BIGINT REFERENCES metrics_exchange.alliance_point (id),
    version           INTEGER,
    created_at        TIMESTAMPTZ
);

CREATE TABLE metrics_exchange.transfer_request
(
    id               BIGSERIAL PRIMARY KEY                           NOT NULL,
    from_company_id  BIGINT REFERENCES metrics_exchange.company (id) NOT NULL,
    to_company_id    BIGINT REFERENCES metrics_exchange.company (id) NOT NULL,
    from_profile_id  VARCHAR(250)                                    NOT NULL,
    to_profile_id    VARCHAR(250)                                    NOT NULL,
    comment          VARCHAR(500),
    decision         VARCHAR(50),
    status           VARCHAR(50),
    decision_comment VARCHAR(500),
    version          INTEGER,
    created_at       TIMESTAMPTZ
);
