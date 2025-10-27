package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private UserDbStorage userStorage;

	@Autowired
	private FilmDbStorage filmStorage;

	@Test
	void contextLoads() {
	}

}
