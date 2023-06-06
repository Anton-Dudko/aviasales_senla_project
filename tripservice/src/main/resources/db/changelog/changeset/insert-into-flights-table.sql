-- --luquibase formatted sql
-- --changeset NikitaNasevich:insert-into-flights-table

INSERT INTO flights (trip_id, airplane_id, flight_number, departure, arrival, meal, hand_luggage, luggage, duration,
                     canceled)
-- minsk - moscow
VALUES (1, 1, 'B2-14400', '2023-06-12 21:30:00.000000', '2023-06-12 22:30:00.000000', true, true, true, 60, false),
       (2, 1, 'B2-14410', '2023-06-13 14:30:00.000000', '2023-06-13 15:30:00.000000', true, true, true, 60, false),
       (1, 2, 'B2-14400', '2023-06-12 21:30:00.000000', '2023-06-12 22:20:00.000000', true, true, true, 50, false),
       (2, 2, 'B2-14410', '2023-06-13 14:30:00.000000', '2023-06-13 15:20:00.000000', true, true, true, 50, false),

-- brest - minsk
       (3, 3, 'B2-9990', '2023-06-14 11:10:00.000000', '2023-06-14 11:50:00.000000', true, true, true, 40, false),
       (4, 3, 'B2-9980', '2023-06-15 13:10:00.000000', '2023-06-15 13:50:00.000000', false, true, true, 40, false),

-- minsk - istanbul
       (5, 2, 'B2-10100', '2023-06-12 11:05:00.000000', '2023-06-12 16:00:00.000000', true, true, true, 295, false),
       (6, 2, 'B2-10110', '2023-06-13 21:30:00.000000', '2023-06-14 02:25:00.000000', true, true, true, 295, false),

-- average flight minsk - new-york
       (7, 4, 'B2-0070', '2023-06-12 01:50:00.000000', '2023-06-12 13:50:00.000000', true, true, true, 720, false),
       (8, 4, 'B2-0080', '2023-06-13 13:50:00.000000', '2023-06-14 01:50:00.000000', true, true, true, 720, false),

-- the fastest flight minsk - new-york without meal
       (7, 5, 'B2-0060', '2023-06-12 01:50:00.000000', '2023-06-12 13:30:00.000000', false, true, true, 700, false),
       (8, 5, 'B2-0050', '2023-06-13 13:50:00.000000', '2023-06-14 01:30:00.000000', false, true, true, 700, false),

-- the longest flight minsk - new-york without meal and luggage
       (7, 3, 'B2-0100', '2023-06-12 00:30:00.000000', '2023-06-12 17:30:00.000000', false, true, false, 1020, false),
       (8, 3, 'B2-0110', '2023-06-13 01:50:00.000000', '2023-06-13 18:50:00.000000', false, true, false, 1020, false),

-- average flight minsk - new-york
       (7, 4, 'B2-0120', '2023-06-12 01:30:00.000000', '2023-06-12 13:30:00.000000', true, false, true, 720, false),
       (8, 4, 'B2-0130', '2023-06-13 13:30:00.000000', '2023-06-14 01:30:00.000000', true, false, true, 720, false),

-- minsk - warsaw - without luggage
       (9, 1, 'B2-25250', '2023-06-12 17:05:00.000000', '2023-06-12 18:35:00.000000', true, true, false, 90, false),
       (10, 1, 'B2-25260', '2023-06-13 19:10:00.000000', '2023-06-13 20:40:00.000000', true, true, false, 90, false),

-- minsk - warsaw - without luggage
       (11, 5, 'B2-001', '2023-06-12 18:15:00.000000', '2023-06-13 06:45:00.000000', true, true, false, 750, false),
       (12, 5, 'B2-002', '2023-06-13 19:10:00.000000', '2023-06-14 07:40:00.000000', true, true, false, 750, false);