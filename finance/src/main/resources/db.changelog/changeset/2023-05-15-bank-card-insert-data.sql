--liquibase formatted sql
--changeset Pavel:insert-data

INSERT INTO public.bank_card (id, account_sum, card_date, card_holder, card_number, cvv) VALUES (1, 3000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E80B78', 'Pavle', '6011000991300009', '1314');
INSERT INTO public.bank_card (id, account_sum, card_date, card_holder, card_number, cvv) VALUES (2, 5000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007EA0C78', null, '60115564485789458', '123');
INSERT INTO public.bank_card (id, account_sum, card_date, card_holder, card_number, cvv) VALUES (3, 10000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E90B78', 'Serg Ivanov', '3566000020000410', '167');
INSERT INTO public.bank_card (id, account_sum, card_date, card_holder, card_number, cvv) VALUES (4, 100.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E90B78', 'Amigo', '4485349439401891', '299');
INSERT INTO public.bank_card (id, account_sum, card_date, card_holder, card_number, cvv) VALUES (5, 10000.00, E'\\xACED00057372000D6A6176612E74696D652E536572955D84BA1B2248B20C0000787077060C000007E90B78', 'US HOLDER', '6011000000000087', '299');


INSERT INTO blocked_card (id, blocked_date, card_number) VALUES (1, '2023-06-08', '6011000000000087');

INSERT INTO blocked_country (id, blocked_date, country_code) VALUES (1, '2023-06-08', 'AR');

INSERT INTO public.payment_status (id, status) VALUES (1, 'PENDING');
INSERT INTO public.payment_status (id, status) VALUES (2, 'PAID');
INSERT INTO public.payment_status (id, status) VALUES (3, 'FAILED');
INSERT INTO public.payment_status (id, status) VALUES (4, 'REFUND');