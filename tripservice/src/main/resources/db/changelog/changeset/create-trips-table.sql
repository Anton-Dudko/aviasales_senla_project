--luquibase formatted sql
--changeset NikitaNasevich:create-trip-table

CREATE TABLE trips
(
    trip_id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    departure_city varchar(255) NOT NULL,
    arrival_city varchar(255) NOT NULL,
    unique (departure_city, arrival_city)
)