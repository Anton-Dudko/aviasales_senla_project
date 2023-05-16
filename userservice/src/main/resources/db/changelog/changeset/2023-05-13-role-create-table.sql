--liquibase formatted sql
--changeset OlgaGlavdel:role-create-table

CREATE TYPE role AS ENUM ('ROLE_ADMIN', 'ROLE_USER');