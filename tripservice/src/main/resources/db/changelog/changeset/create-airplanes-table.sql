--luquibase formatted sql
--changeset NikitaNasevich:create-airplane-table

CREATE TABLE airplanes
(
    airplane_id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    model varchar(255) NOT NULL,
    number_of_seats int NOT NULL,
    socket bool DEFAULT FALSE,
    media bool DEFAULT FALSE,
    wi_fi bool DEFAULT FALSE,
    seat_pitch int,
    seat_width int,
    has_first_class bool DEFAULT FALSE,
    chair_scheme varchar(10)
)