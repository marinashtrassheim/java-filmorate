package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        Integer mpaRatingId = rs.getObject("mpa_rating", Integer.class);
        if (mpaRatingId != null) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaRatingId);
            mpa.setName(rs.getString("mpa_name"));
            mpa.setDescription(rs.getString("mpa_description"));
            film.setMpa(mpa);
        }

        LocalDate releaseDate = rs.getObject("release_date", LocalDate.class);
        film.setReleaseDate(releaseDate);

        film.setDuration(rs.getInt("duration"));

        return film;
    }
}
