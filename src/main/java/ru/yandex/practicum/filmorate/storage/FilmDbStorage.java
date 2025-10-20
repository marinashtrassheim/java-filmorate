package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.CanNotAddLikeException;
import ru.yandex.practicum.filmorate.exception.CanNotDeleteLikeException;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Film createFilm(Film film) {
        try {
            String sql = "INSERT INTO films (name, " +
                    "description, " +
                    "release_date, " +
                    "duration, " +
                    "mpa_rating) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            if (film.getMpa() != null && !isMpaExists(film.getMpa().getId())) {
                throw new NotFoundException("MPA с id " + film.getMpa().getId() + " не существует");
            }

            if (film.getGenres() != null) {
                Set<Genre> genres = film.getGenres();
                for (Genre genre : genres) {
                    if (!isGenreExists(genre.getId())) {
                        throw new NotFoundException("Жанра с id " + genre.getId() + " не существует");
                    }
                }
            }

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
                ps.setLong(4, film.getDuration());

                if (film.getMpa() != null) {
                    ps.setInt(5, film.getMpa().getId());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                return ps;
            }, keyHolder);

            film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            int newFilmId = Objects.requireNonNull(keyHolder.getKey()).intValue();

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                updateFilmGenres(film);
            }

            return getFilm(newFilmId);
        } catch (DatabaseException e) {
            throw new DatabaseException("Ошибка сохранения данных: проверьте корректность MPA рейтинга");
        }

    }

    @Override
    public Film updateFilm(Film newFilm) {
        String sql = "UPDATE films SET " +
                "name = ?, " +
                "description = ?, " +
                "mpa_rating = ?, " +
                "release_date = ?, " +
                "duration = ? " +
                "WHERE id = ?";

        if (!isFilmExists(newFilm.getId())) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }

        if (newFilm.getMpa() != null && !isMpaExists(newFilm.getMpa().getId())) {
            throw new NotFoundException("MPA с id " + newFilm.getMpa().getId() + " не существует");
        }

        jdbcTemplate.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getMpa() != null ? newFilm.getMpa().getId() : null, // Может быть null
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getId());
        updateFilmGenres(newFilm);
        return newFilm;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT f.*, m.code as mpa_code, m.name as mpa_name, m.description as mpa_description " +
                "FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating = m.id";
        Collection<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
            film.setUserLikes(getFilmLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film getFilm(int filmId) {
        String sql = "SELECT f.*, m.code as mpa_code, m.name as mpa_name, m.description as mpa_description " +
                "FROM films f LEFT JOIN mpa_rating m ON f.mpa_rating = m.id " +
                "WHERE f.id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, filmId);
            if (film != null) {
                film.setGenres(getFilmGenres(filmId));
                film.setUserLikes(getFilmLikes(filmId));
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public boolean isFilmExists(int filmId) {
        String sql = "SELECT id FROM films WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Integer.class, filmId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        if (!isLikeExists(filmId, userId)) {
            jdbcTemplate.update(sql, filmId, userId);
        } else {
            throw new CanNotAddLikeException("Пользователь уже ставил лайк фильму");
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        if (isLikeExists(filmId, userId)) {
            jdbcTemplate.update(sql, filmId, userId);
        } else {
            throw new CanNotDeleteLikeException("Лайка пользователя фильму не существует");
        }
    }

    private boolean isLikeExists(int filmId, int userId) {
        String sql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return count != null && count > 0;
    }

    private boolean isMpaExists(int mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa_rating WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count != null && count > 0;
    }

    private boolean isGenreExists(int genreId) {
        String sql = "SELECT COUNT(*) FROM genre WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    private Set<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.* FROM genre g " +
                "JOIN film_genre fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, new GenreRowMapper(), filmId));
    }

    private Set<Integer> getFilmLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
    }

    private void updateFilmGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());
        Set<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : genres) {
                jdbcTemplate.update(insertSql, film.getId(), genre.getId());
            }
        }
    }
}
