create table IF NOT EXISTS ticket
(
    id      bigint not null primary key generated always as identity ,
    fio     varchar(255),
    price   numeric(38, 2),
    status  varchar(255),
    flight_id bigint,
    type    varchar(255),
    user_id bigint,
    seat_number integer
);
