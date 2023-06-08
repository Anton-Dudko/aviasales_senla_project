--liquibase formatted sql
--changeset Pavel:create-tables


create table bank_card
(
    id          bigint generated by default as identity
        primary key,
    account_sum numeric(19, 2),
    card_date   bytea,
    card_holder varchar(255),
    card_number varchar(255),
    cvv         varchar(255)
);


create table blocked_card
(
    id           bigint generated by default as identity
        primary key,
    blocked_date date,
    card_number  varchar(255)
);

create table blocked_country
(
    id           bigint generated by default as identity
        primary key,
    blocked_date date,
    country_code varchar(255)
);

create table payment_status
(
    status varchar,
    id     integer not null
        constraint id
            primary key
);

create table payments
(
    id                         bigint generated by default as identity,
    amount                     numeric(19, 2),
    card_number                varchar(255),
    payment_creation_date_time timestamp,
    payment_status             integer,
    user_id                    bigint,
    primary key (id),
    constraint status_fk
        foreign key (payment_status) references payment_status
);

create table payment_ticket
(
    payment_id bigint not null,
    ticket_id  bigint,
    constraint fkd1chexruyo9yky2lb4eqe2o3t
        foreign key (payment_id) references payments
);
