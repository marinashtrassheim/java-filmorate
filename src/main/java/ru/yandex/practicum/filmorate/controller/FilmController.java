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
    Map<Integer, Film> films = new HashMap<>();
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

    @PutMapping("/{id}")
    public Film updateFilm(@Valid @RequestBody Film newFilm, @PathVariable int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        newFilm.setId(id);
        films.put(id, newFilm);
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
