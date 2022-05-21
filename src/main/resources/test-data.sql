INSERT INTO Customer (username, name, password, email, birth_date, sex) VALUES ('test1', '테스트1', '$2a$10$4YFtzh4mOHs1N4ewkrArleoIIVFqjdkW1rzU/lJjIixMSH88mufd2', 'd@d.com', '2007-10-04', 'M');
INSERT INTO Customer (username, name, password, email, birth_date, sex) VALUES ('test2', '테스트2', '$2a$10$wKmh8o0KUwpB3h6lI7Rew.SGVfmlkPVftsueMn0.fsTtJDTft2zuy', 'e@e.com', '2020-10-10', 'M');
INSERT INTO Customer (username, name, password, email, birth_date, sex) VALUES ('test3', '테스트3', '$2a$10$/j4uWZWdoHd8BUkA6X1cpeh9iZAOsfozRBYEGqmDGflzwPLO9bWOa', 'e@e.com', '2020-10-10', 'M');
INSERT INTO Customer (username, name, password, email, birth_date, sex) VALUES ('test_wo_authority', '테스트4', '$2a$10$/j4uWZWdoHd8BUkA6X1cpeh9iZAOsfozRBYEGqmDGflzwPLO9bWOa', 'e@e.com', '2020-10-10', 'M');

INSERT INTO Authority (username, authority_name) VALUES ('test1', 'ADMIN');
INSERT INTO Authority (username, authority_name) VALUES ('test1', 'USER');
INSERT INTO Authority (username, authority_name) VALUES ('test2', 'USER');
INSERT INTO Authority (username, authority_name) VALUES ('test3', 'USER');


INSERT INTO Movie (mid, title, open_day, director, rating, length) VALUES (1, '영화테스트1', '2022-05-09', '감독1', 'ALL', 126);
INSERT INTO Movie (mid, title, open_day, director, rating, length) VALUES (2, '영화테스트2', '2022-05-10', '감독2', '_12', 126);
INSERT INTO Movie (mid, title, open_day, director, rating, length) VALUES (3, '영화테스트3', '2022-05-14', '감독3', '_15', 106);
INSERT INTO Movie (mid, title, open_day, director, rating, length) VALUES (4, '영화테스트4', '2022-05-11', '감독4', '_18', 127);

INSERT INTO Actor (mid, name) VALUES (1, '배우1_1');
INSERT INTO Actor (mid, name) VALUES (1, '배우1_2');
INSERT INTO Actor (mid, name) VALUES (1, '배우1_3');
INSERT INTO Actor (mid, name) VALUES (1, '배우1_4');
INSERT INTO Actor (mid, name) VALUES (2, '배우2_1');
INSERT INTO Actor (mid, name) VALUES (2, '배우2_2');
INSERT INTO Actor (mid, name) VALUES (2, '배우2_3');
INSERT INTO Actor (mid, name) VALUES (2, '배우2_4');
INSERT INTO Actor (mid, name) VALUES (2, '배우2_5');
INSERT INTO Actor (mid, name) VALUES (2, '배우2_6');
INSERT INTO Actor (mid, name) VALUES (3, '배우3_1');
INSERT INTO Actor (mid, name) VALUES (3, '배우3_2');
INSERT INTO Actor (mid, name) VALUES (3, '배우3_3');
INSERT INTO Actor (mid, name) VALUES (3, '배우3_4');
INSERT INTO Actor (mid, name) VALUES (3, '배우3_5');
INSERT INTO Actor (mid, name) VALUES (4, '배우4_1');
INSERT INTO Actor (mid, name) VALUES (4, '배우4_2');
INSERT INTO Actor (mid, name) VALUES (4, '배우4_3');

INSERT INTO Theater (tname, seats) VALUES ('경기', 70);
INSERT INTO Theater (tname, seats) VALUES ('대전', 85);
INSERT INTO Theater (tname, seats) VALUES ('부산', 65);
INSERT INTO Theater (tname, seats) VALUES ('서울', 65);
INSERT INTO Theater (tname, seats) VALUES ('세종', 60);

INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (1, 2, '경기', '2022-05-09 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (2, 2, '경기', '2022-05-09 13:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (3, 2, '세종', '2022-05-09 12:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (4, 2, '세종', '2022-05-09 15:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (5, 2, '세종', '2022-05-10 15:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (6, 2, '세종', '2022-05-19 12:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (7, 3, '서울', '2022-05-24 12:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (8, 3, '서울', '2022-05-24 15:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (9, 3, '부산', '2022-05-24 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (10, 3, '부산', '2022-05-24 15:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (11, 3, '부산', '2022-06-02 10:30:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (12, 4, '대전', '2022-05-11 10:30:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (13, 4, '대전', '2022-05-11 15:30:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (14, 4, '대전', '2022-05-12 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (15, 4, '부산', '2022-05-12 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (16, 4, '부산', '2022-05-15 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (17, 4, '부산', '2022-05-18 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (18, 2, '서울', '2022-05-11 10:00:00.0');
INSERT INTO Schedule (sid, mid, tname, show_at) VALUES (19, 4, '부산', '2022-05-09 10:00:00.0');
