package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    private Film testFilm;
    private User testUser;

    @BeforeEach
    void setUp() {
        filmStorage.getFilms().clear();
        userStorage.getUsers().clear();

        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("tester");
        testUser.setName("Test User");
        userStorage.createUser(testUser);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        filmStorage.createFilm(testFilm);
    }

    @Test
    void setLike_ShouldAddLikeToFilm() {
        Film result = filmService.setLike(testFilm.getId(), testUser.getId());

        assertTrue(result.hasLike(testUser.getId()), "Лайк должен быть добавлен к фильму");
        assertEquals(1, result.getLikesCount(), "Количество лайков должно быть 1");
    }

    // Тест для deleteLike
    @Test
    void deleteLike_ShouldRemoveLikeFromFilm() {
        filmService.setLike(testFilm.getId(), testUser.getId());

        Film result = filmService.deleteLike(testFilm.getId(), testUser.getId());

        assertFalse(result.hasLike(testUser.getId()), "Лайк должен быть удален из фильма");
        assertEquals(0, result.getLikesCount(), "Количество лайков должно быть 0");
    }
}

