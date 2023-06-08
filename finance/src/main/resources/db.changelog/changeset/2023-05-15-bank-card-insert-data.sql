--liquibase formatted sql
--changeset Pavel:insert-data

INSERT INTO public.bank_card (account_sum, card_date, card_holder, card_number, cvv) VALUES (3000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E80B78', 'Pavle', '6011000991300009', '1314');
INSERT INTO public.bank_card (account_sum, card_date, card_holder, card_number, cvv) VALUES (5000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007EA0C78', null, '60115564485789458', '123');
INSERT INTO public.bank_card (account_sum, card_date, card_holder, card_number, cvv) VALUES (10000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E90B78', 'Serg Ivanov', '3566000020000410', '167');
INSERT INTO public.bank_card (account_sum, card_date, card_holder, card_number, cvv) VALUES (100.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E90B78', 'Amigo', '4485349439401891', '299');
INSERT INTO public.bank_card (account_sum, card_date, card_holder, card_number, cvv) VALUES (10000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E90B78', 'US HOLDER', '6011000000000087', '299');
INSERT INTO public.bank_card (account_sum, card_date, card_holder, card_number, cvv) VALUES (50.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007F10B78', 'ISRAEL USER', '4263982640269299', '999');


INSERT INTO blocked_card (blocked_date, card_number) VALUES ('2023-06-08', '6011000000000087');

INSERT INTO blocked_country (blocked_date, country_code) VALUES ('2023-06-08', 'AR');

INSERT INTO public.payment_status (id, status) VALUES (1, 'PENDING');
INSERT INTO public.payment_status (id, status) VALUES (2, 'PAID');
INSERT INTO public.payment_status (id, status) VALUES (3, 'FAILED');
INSERT INTO public.payment_status (id, status) VALUES (4, 'REFUND');

INSERT INTO public.payments (amount, card_number, payment_creation_date_time, payment_status, user_id) VALUES (100.00, '6011000991300009', '2023-03-10 23:02:58.000000', 2, 2);
INSERT INTO public.payments (amount, card_number, payment_creation_date_time, payment_status, user_id) VALUES (100.00, '6011000991300009', '2023-03-10 23:02:58.000000', 2, 2);
INSERT INTO public.payments (amount, card_number, payment_creation_date_time, payment_status, user_id) VALUES (200.00, '6011000991300009', '2023-03-10 23:02:58.000000', 2, 2);

INSERT INTO public.payment_ticket (payment_id, ticket_id) VALUES (1, 5);
INSERT INTO public.payment_ticket (payment_id, ticket_id) VALUES (2, 13);
INSERT INTO public.payment_ticket (payment_id, ticket_id) VALUES (3, 53);
INSERT INTO public.payment_ticket (payment_id, ticket_id) VALUES (3, 61);
