--liquibase formatted sql
--changeset OlgaGlavdel:users-insert-data

INSERT INTO public.users (id, username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES (1, 'admin1', 'olga.glavdel@gmaaaaail.com', '$2a$13$WxgzgtLhLPCPxbNKVTzQtu2xcA2Q6dH6aMB1065.GESsiH9SOwMKi',
        '2009-09-09', 'EN', 'ROLE_ADMIN',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjEiLCJleHAiOjE2ODgzMDc1Nzl9.WQGL2JjhmWi5YNiBZn7rwegqtv7AsgZo4Rc77ZMXtXwfLJb3Egg2LPMVy_-BuJFLxiyAyOgY1JlQwxisLKNbSA',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjEiLCJleHAiOjE2ODY1Nzk1NzksInJvbGUiOiJST0xFX0FETUlOIn0.U9B8-ZI0UJKss0GtvT-bKVAIkj7qm519Y34FJXt2wjzSsVW1QyzBaYjxmadaYY3oiYFYFZByjYBOU51urxHt7w');
INSERT INTO public.users (id, username, email, password, date_birth, language, role, refresh_token, access_token)
VALUES (2, 'username1', 'pin007@tuuuuuut.by', '$2a$13$CfxofPy.BNHGH/5Jp/Q3K.cEjq8F6fg3nGa2AGZV8AA8nLzqXYXLK',
        '2012-12-12', 'RU', 'ROLE_USER',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZTEiLCJleHAiOjE2ODgzMDc1OTB9.3i3mBpWWs2C2Fa9_YuqR7PDyp_hhhw-0luUB31fbpbnGCDJIggfRSqDvuXUzeE4wQqKf0Ulj0pVhYvHHrdJCJg',
        'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZTEiLCJleHAiOjE2ODY1Nzk1OTAsInJvbGUiOiJST0xFX1VTRVIifQ.AFHFVo_LQxnfb1-CMGAAHYSqtHZ968577Ibh8FZ84L0fUWPf0hupqBj6G2vF14RhQxjosOBOCFniT2QOlpk6OQ');
