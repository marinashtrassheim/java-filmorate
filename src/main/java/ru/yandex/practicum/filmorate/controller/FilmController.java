package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    public Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Фильм создан: ID={}, name={}, description={}",
                film.getId(), film.getName(), film.getDescription());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }
        newFilm.setId(newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм изменен: ID={}, name={}, description={}",
                newFilm.getId(), newFilm.getName(), newFilm.getDescription());
        return newFilm;
    }

    private int getNextFilmId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }

}
