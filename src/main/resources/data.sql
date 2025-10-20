MERGE INTO genre (id, name) KEY(id)
VALUES
(1, 'COMEDY'),
(2, 'DRAMA'),
(3, 'ANIMATION'),
(4, 'THRILLER'),
(5, 'DOCUMENTARY'),
(6, 'ACTION'),
(7, 'OTHER');

MERGE INTO mpa_rating (id, code, name, description) KEY(id)
VALUES
(1, 'G', 'G', 'у фильма нет возрастных ограничений'),
(2, 'PG', 'PG', 'детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'PG-13', 'детям до 13 лет просмотр не желателен'),
(4, 'R', 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
(5, 'NC-17', 'NC-17', 'лицам до 18 лет просмотр запрещён');