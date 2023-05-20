--liquibase formatted sql
--changeset OlgaGlavdel:users-create-table

CREATE TABLE users
(
    id       bigserial NOT NULL,
    username varchar   NOT NULL,
    email    varchar   NOT NULL UNIQUE,
    password varchar   NOT NULL,
    role     role      NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);