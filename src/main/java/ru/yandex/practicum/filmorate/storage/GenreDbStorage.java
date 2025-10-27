package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    public Genre getGenre(int id) {
        String sql = "SELECT id, name FROM genre WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, genreRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id " + id + " не найден");
        }
    }

    public Collection<Genre> getAllGenres() {
        String sql = "SELECT id, name FROM genre";
        return jdbcTemplate.query(sql, genreRowMapper);
    }
}
