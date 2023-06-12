--liquibase formatted sql
--changeset OlgaGlavdel:users-insert-data

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('OlgaAdmin11', 'pin007@tut.by', '$2a$13$dUT/eR7720NppKFg/CG0lu.YpflpHF72wpixBI9POUtOIi/PFINc6', '2009-09-09',
        'EN', 'ROLE_ADMIN',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW4wMDdAdHV0LmJ5IiwiZXhwIjoxNjg5MTAyNTUwfQ.7yYPY6nL22a6vgQTTEnm4lMnz3ilZUnK-wCKzk5odURJ9XnfKXgp0tmtuWWxLRp0U5KMw9FQuE-LmnZG1HlGOQ',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW4wMDdAdHV0LmJ5IiwiZXhwIjoxNjg3Mzc0NTUwLCJyb2xlIjoiUk9MRV9BRE1JTiJ9.Al0AMjMxTTWfFTxI2VbCqp_6aQefMRpD31F3Juc6IicTegZpoiL_nGgmYZIjBEvKIw5zn6lZ9TzoiTScqcSBIQ');

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('username11', 'olga.glavdel@gmail.com', '$2a$13$prJByZAYX0a3lEMYpH19yuTyzp7ST.td3hFy0sWNt5BnYxEgXsSKu',
        '1999-01-11', 'EN', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvbGdhLmdsYXZkZWxAZ21haWwuY29tIiwiZXhwIjoxNjg5MTAyNjEyfQ.sqa7kdZMRhVhQMelLz3mJSM9eqWd9_BZgFE-kh-R8NSMv5hxFA07EbKl7vgShDBt61hMsSb4vrXVHQJAmIjJkA',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvbGdhLmdsYXZkZWxAZ21haWwuY29tIiwiZXhwIjoxNjg3Mzc0NjEyLCJyb2xlIjoiUk9MRV9VU0VSIn0.8KEA-RhUrwp2B7qlZlxJEuYY8uqqocAFGyGKEqFNq9GZ_T1FgjnQv2ttPP2BezLXB7PofZqJmWrvcM9DbmL_xQ');

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('username22', 'pin@gmaaaail.com', '$2a$13$MnFTzKpp1lZHfCumONcKWeXuRmQyee1S.F2tzZ.qNhbUip18nzVw2', '2009-09-19',
        'RU', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW5AZ21hYWFhaWwuY29tIiwiZXhwIjoxNjg5MTAyNjQ5fQ.aN4h3yJmrmsS6rbqWuckZeELQKu_zA2o9uoZtMkgQhOP223ZSUK6LXteaSTL9tZEaM0ZzBNKJ4KeMZicDu5Kew',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW5AZ21hYWFhaWwuY29tIiwiZXhwIjoxNjg3Mzc0NjQ5LCJyb2xlIjoiUk9MRV9VU0VSIn0.b1EzGlcalSpGC0bXFBefGiG-_vCdKlxXQgESkBD76TUMjHeNEOBfgTod7Y8pmRM16Gb7aiwQ9nLqO-u_yDwmvA');

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('username33', 'pin@gmaaaaail.com', '$2a$13$QKb/H746BJYsSsM/e1t3ZezHhmyo02xZsCOg8kIyWLkChhsqyEaPG', '2000-12-05',
        'RU', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW5AZ21hYWFhYWlsLmNvbSIsImV4cCI6MTY4OTEwMjY3OH0.0d7JkUy8zZhhPNwaKMEbZ9imOb-W_Ndiuevnof8i1VTpUPcOmMF_pPYbO0v2BJGk565Ylgi06sOsPIaWgfjAnw',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW5AZ21hYWFhYWlsLmNvbSIsImV4cCI6MTY4NzM3NDY3OCwicm9sZSI6IlJPTEVfVVNFUiJ9.3MAnHTN5rO1xV3Gob-Q2jP4wrbqhHzBLtR-d7WQCQ_8NI82mHSgG4HbniFFyykCiiGmk6_ujWeAIKbePkn-vbA');

--rollback delete from users where username='OlgaAdmin11';
--rollback delete from users where username='username11';
--rollback delete from users where username='username22';
--rollback delete from users where username='username33';