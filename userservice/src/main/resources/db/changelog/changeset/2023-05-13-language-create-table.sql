--liquibase formatted sql
--changeset OlgaGlavdel:language-create-table

CREATE TYPE language AS ENUM ('EN', 'RU');