--luquibase formatted sql
--changeset NikitaNasevich:insert-into-airplane-table

INSERT INTO airplanes (model, number_of_seats, socket, media, wi_fi, seat_pitch, seat_width, chair_scheme)
VALUES ('Boeing 737', 40, false, false, false, 36, 36, '3-3');
INSERT INTO airplanes (model, number_of_seats, socket, media, wi_fi, seat_pitch, seat_width, chair_scheme)
VALUES ('Boeing 747', 50, true, true, true, 38, 37, '3-3');
INSERT INTO airplanes (model, number_of_seats, socket, media, wi_fi, seat_pitch, seat_width, chair_scheme)
VALUES ('Ту-154', 30, true, false, true, 35, 34, '4-4');
INSERT INTO airplanes (model, number_of_seats, socket, media, wi_fi, seat_pitch, seat_width, chair_scheme)
VALUES ('Airbus A300', 50, true, true, true, 38, 38, '3-3');
INSERT INTO airplanes (model, number_of_seats, socket, media, wi_fi, seat_pitch, seat_width, chair_scheme, has_first_class)
VALUES ('Embraer E-Jet E2', 30, true, false, false, 37, 36, '2-2', true);