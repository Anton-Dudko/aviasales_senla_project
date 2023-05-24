--luquibase formatted sql
--changeset NikitaNasevich:create-subscriptions-table

CREATE TABLE subscriptions
(
    subscription_id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    event_name varchar(255) NOT NULL,
    user_id bigint NOT NULL,
    trip_flight_id bigint NOT NULL
)