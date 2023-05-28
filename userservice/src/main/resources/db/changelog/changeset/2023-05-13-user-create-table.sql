--liquibase formatted sql
--changeset OlgaGlavdel:users-create-table

CREATE TABLE users
(
    id            bigserial NOT NULL,
    username      varchar   NOT NULL UNIQUE,
    email         varchar   NOT NULL UNIQUE,
    password      varchar   NOT NULL,
    date_birth    date      NOT NULL,
    language      language,
    role          role      NOT NULL,
    refresh_token varchar   NOT NULL,
    access_token  varchar   NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

