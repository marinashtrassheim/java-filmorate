# java-filmorate
## Схема базы данных приложения filmorate
В схеме есть две таблицы, которые представляют основные сущности приложения filmorate - таблица с фильмами film и таблица с пользователями user. Запросы на добавления в друзья пользователей друг друг хранятся в таблице friendship, поле status может принимать 3 значения 'REQUESTED' - запрос на добавление в друзья отправлен, 'CONFIRMED' - запрос на добавления в друзья одобрен, 'REJECTED' - запрос на добавление в друзья отклонен.
Жанры фильмов хранятся в отдельной таблице справочнике genre, а так как у одного фильма может много жанров и у одного жанра много фильмов - связываются таблицы film и genre через отдельную таблицу film_genre.
Все лайки, который поставили пользователи тоже имеют свою связывающую user-film таблицу film_likes
Знак ? в quickdatabasediagramm означает что поле может быть NULL

<img width="1040" height="413" alt="Схема БД filmorate" src="https://github.com/user-attachments/assets/d4e79ddd-cc36-41b3-82cd-7cec7d9a4f5f" />

## Популярные запросы
Топ-5 самых популярных фильмов по количеству лайков
 ```
SELECT 
    f.id,
    f.name,
    f.rating,
    COUNT(fl.user_id) AS likes_count,
    STRING_AGG(DISTINCT g.name, ', ') AS genres
FROM film f
LEFT JOIN film_likes fl ON f.id = fl.film_id
LEFT JOIN film_genre fg ON f.id = fg.film_id
LEFT JOIN genre g ON fg.genre_id = g.id
GROUP BY f.id, f.name, f.rating
ORDER BY likes_count DESC
LIMIT 5;
```

Статистика по жанрам

```
SELECT 
    g.name AS genre,
    COUNT(DISTINCT fg.film_id) AS films_count,
    COUNT(fl.user_id) AS total_likes,
    ROUND(AVG(f.duration), 2) AS avg_duration
FROM genre g
LEFT JOIN film_genre fg ON g.id = fg.genre_id
LEFT JOIN film f ON fg.film_id = f.id
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY g.id, g.name
ORDER BY total_likes DESC;
```
Найти общих друзей между двумя пользователями

```
SELECT 
    u.id,
    u.name,
    u.email
FROM user u
WHERE u.id IN (
    -- Друзья первого пользователя
    SELECT 
        CASE 
            WHEN f.user_id = 1 THEN f.friend_id
            WHEN f.friend_id = 1 THEN f.user_id
        END AS friend_id
    FROM friendship f
    WHERE (f.user_id = 1 OR f.friend_id = 1)
    AND f.status = 'CONFIRMED'
    
    INTERSECT  -- Пересечение с друзьями второго пользователя
    
    -- Друзья второго пользователя
    SELECT 
        CASE 
            WHEN f.user_id = 2 THEN f.friend_id
            WHEN f.friend_id = 2 THEN f.user_id
        END AS friend_id
    FROM friendship f
    WHERE (f.user_id = 2 OR f.friend_id = 2)
    AND f.status = 'CONFIRMED'
);
```
