package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Set;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class})
class FilmDbStorageIntegrationTest {

    private final FilmDbStorage filmStorage;
    private Film film;
    private final UserDbStorage userStorage;
    private User user;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.createFilm(film);

        user = new User();
        user.setEmail("user101@mail.ru");
        user.setLogin("user101");
        user.setName("User One");
        userStorage.createUser(user);
    }

    @Test
    void createFilm() {
        Film filmFromDb = filmStorage.getFilm(film.getId());
        assertEquals("Test Film", filmFromDb.getName());
        assertEquals("Test Description", filmFromDb.getDescription());
    }

    @Test
    void updateFilm() {
        Film existingFilm = filmStorage.getFilm(film.getId());

        existingFilm.setName("Update Test Film");
        existingFilm.setDescription("Update Test Description");
        existingFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        existingFilm.setDuration(150);
        Mpa mpa = new Mpa();
        mpa.setId(2);
        existingFilm.setMpa(mpa);

        filmStorage.updateFilm(existingFilm);

        Film filmFromDb = filmStorage.getFilm(existingFilm.getId());

        assertEquals("Update Test Film", filmFromDb.getName());
        assertEquals("Update Test Description", filmFromDb.getDescription());
        assertEquals(LocalDate.of(2020, 2, 2), filmFromDb.getReleaseDate());
        assertEquals(150, filmFromDb.getDuration());
        assertEquals(2, filmFromDb.getMpa().getId());
    }

    @Test
    void shouldAddUserLike() {
        Film filmToAddLike = filmStorage.getFilm(film.getId());
        User userLike = userStorage.getUser(user.getId());

        filmStorage.addLike(filmToAddLike.getId(), userLike.getId());
        Set<Integer> filmLikes = filmStorage.getFilmLikes(film.getId());
        assertThat(filmLikes.contains(userLike.getId())).isTrue();
    }
}