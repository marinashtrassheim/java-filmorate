package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;

@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRowMapper = mpaRowMapper;
    }

    public Mpa getMpa(int mpaId) {
        String sql = "SELECT id, name, description FROM mpa_rating WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mpaRowMapper, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa с id " + mpaId + " не найден");
        }
    }

    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT id, name, description FROM mpa_rating";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

}
