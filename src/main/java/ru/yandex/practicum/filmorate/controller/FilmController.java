package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<?> getFilms() {
        try {
            Collection<Film> films = filmStorage.getFilms();
            return ResponseEntity.ok(films);
        } catch (Exception e) {
            log.error("Ошибка при получении списка фильмов", e);
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера");
        }
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmStorage.getFilm(id);
    }

    @GetMapping("/popular")
    private Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.topFilms(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        try {
            Film createdFilm = filmStorage.createFilm(film);
            log.info("Фильм создан: ID={}, name={}, description={}",
                    createdFilm.getId(), createdFilm.getName(), createdFilm.getDescription());
            return createdFilm;
        } catch (NotFoundException e) {
            log.warn("Ошибка при создании фильма: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании фильма", e);
            throw new RuntimeException("Внутренняя ошибка сервера", e);
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        Film updatedFilm = filmStorage.updateFilm(newFilm);
        log.info("Фильм изменен: ID={}, name={}, description={}",
                newFilm.getId(), newFilm.getName(), newFilm.getDescription());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmService.setLike(filmId, userId);
        log.info("Фильму ID={} поставлен лайк пользователя ID={}",
                filmId, userId);
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmService.deleteLike(filmId, userId);
        log.info("У фильма ID={} удален лайк пользователя ID={}",
                filmId, userId);
        return film;
    }
}
