create table IF NOT EXISTS ticket
(
    id      bigint not null primary key,
    fio     varchar(255),
    price   numeric(38, 2),
    status  varchar(255),
    trip_id bigint,
    type    varchar(255),
    user_id bigint
);
