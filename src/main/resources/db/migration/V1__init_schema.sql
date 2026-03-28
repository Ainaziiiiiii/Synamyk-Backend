CREATE TABLE IF NOT EXISTS regions
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id             BIGSERIAL PRIMARY KEY,
    phone          VARCHAR(20)  NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100),
    last_name      VARCHAR(100),
    region_id      BIGINT REFERENCES regions (id),
    role           VARCHAR(20)  NOT NULL DEFAULT 'USER',
    phone_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS otp_codes
(
    id          BIGSERIAL PRIMARY KEY,
    phone       VARCHAR(20)  NOT NULL,
    code        VARCHAR(10)  NOT NULL,
    token       VARCHAR(255) NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    verified    BOOLEAN      NOT NULL DEFAULT FALSE,
    used        BOOLEAN               DEFAULT FALSE,
    used_at     TIMESTAMP,
    expires_at  TIMESTAMP    NOT NULL,
    verified_at TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

-- Seed regions (Kyrgyzstan)
INSERT INTO regions (name, created_at, updated_at)
VALUES ('Бишкек', NOW(), NOW()),
       ('Чуйская область', NOW(), NOW()),
       ('Иссык-Кульская область', NOW(), NOW()),
       ('Нарынская область', NOW(), NOW()),
       ('Таласская область', NOW(), NOW()),
       ('Джалал-Абадская область', NOW(), NOW()),
       ('Ошская область', NOW(), NOW()),
       ('Баткенская область', NOW(), NOW()),
       ('Ош', NOW(), NOW());