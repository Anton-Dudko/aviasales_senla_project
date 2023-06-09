--liquibase formatted sql
--changeset OlgaGlavdel:users-insert-data

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('OlgaAdmin', 'pin007@tut.by', '$2a$13$agzkNNVHksw1m6MrLRiUJexv50XmRkvWUZ9GKtQtlyGuM6KH3EmI2', '2009-09-09',
        'EN', 'ROLE_ADMIN',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW4wMDdAdHV0LmJ5IiwiZXhwIjoxNjg4ODE1MTQ1fQ.cVIdSIa63Clvbo3kCP-F4FV6XJ6KA6g1aM5eG_Yvm1lx749VOfOJyLWZkLyJKG-x445O7ZB1-h6DaKGrfSewVQ',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwaW4wMDdAdHV0LmJ5IiwiZXhwIjoxNjg2MzA5NTQ1LCJyb2xlIjoiUk9MRV9BRE1JTiJ9.e609r83sayyY-5wzLgIw43E7l1rdQkJqbUcltAqG4_f8G9XKkkqQonl8xuJTle8mhQ498OLL4htdEZak6wlF-w');

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('username11', 'olga.glavdel@gmail.com', '$2a$13$MUAdkSqDowYPidowl3w.1uSf0fxElYePqBbmdA8rVyi49KvJCzDb2',
        '2012-12-12', 'RU', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvbGdhLmdsYXZkZWxAZ21haWwuY29tIiwiZXhwIjoxNjg4ODE1MTU0fQ.sW4cdJRkifySEqC6YVzBptReN-U3AvB3HTBcdVlQiliYC-xofbOfH4Z08Ai-a7yYlkLu1qwqGzRxBh6Xs-rpZw',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvbGdhLmdsYXZkZWxAZ21haWwuY29tIiwiZXhwIjoxNjg2MzA5NTU0LCJyb2xlIjoiUk9MRV9VU0VSIn0.UfWJhN-MO8R8Kc_52aWufzYgpEAnmrB1SLFe6B_4QYKKG-BDwbI-orw8pkS6-zwNKrtVJcscSdOfCAPEWjoLdQ');

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('username21', 'someemail@gmaaaail.com', '$2a$13$g84ytPF0k.njXnU2q2Clx.8sOTLOVyDoKjOTGi22idgmK95WNF5Wm',
        '1980-05-10', 'EN', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb21lZW1haWxAZ21hYWFhaWwuY29tIiwiZXhwIjoxNjg4ODE1MTg3fQ.dYPw221C7jLkWTdMK9D5GOrHwQv0hhf67flGpSq5vUIyWw6nj6xTdGFh2vEVQRpOMdh9kt898qiA8LlPsu5HTQ',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb21lZW1haWxAZ21hYWFhaWwuY29tIiwiZXhwIjoxNjg2MzA5NTg3LCJyb2xlIjoiUk9MRV9VU0VSIn0.SOoetBTwHKjJEZtTI1wnGlvoW2lDZW-UN6E8jFeNDEXPQQYnf75qUzcki9ZQThPbrcDJhmvbnExrudkgA9iTzQ');

INSERT INTO users (username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES ('username31', 'someemail@gmaaaaail.com', '$2a$13$XezENL5/oGwt449a.F.0BOx4Rcnoe5K7ljsi18ilWVkt1IKjVAraK',
        '1999-07-13', 'EN', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb21lZW1haWxAZ21hYWFhYWlsLmNvbSIsImV4cCI6MTY4ODgxNTIxNH0.tNslgNBX7CFSXnb3j57ax8xWt6a5IgAeG8T68p5J7Vdd_F-IAVCej5jPvY1qoILFDDtUjQSDfe9RBaSaYh_uoA',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb21lZW1haWxAZ21hYWFhYWlsLmNvbSIsImV4cCI6MTY4NjMwOTYxNCwicm9sZSI6IlJPTEVfVVNFUiJ9.Tgotphde9KsFkYtdnVwDfreW04DuvXQy3UaXvPjKVY2DL8n32_8Tl9YX8wc58ElS3VqqmxrDGAJp5BUEzc5iOA');

--rollback delete users items where username='OlgaAdmin';
--rollback delete users items where username='username11';
--rollback delete users items where username='username21';
--rollback delete users items where username='username31';