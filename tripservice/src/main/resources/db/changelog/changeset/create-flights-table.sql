--luquibase formatted sql
--changeset NikitaNasevich:create-flights-table

CREATE TABLE flights
(
    flight_id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    trip_id bigint references trips(trip_id) ON DELETE CASCADE ,
    airplane_id bigint references airplanes(airplane_id) ON DELETE CASCADE,
    flight_number varchar(255) NOT NULL,
    departure timestamp NOT NULL,
    arrival timestamp NOT NULL,
    meal bool,
    hand_luggage bool,
    luggage bool,
    duration bigint,
    canceled bool
)