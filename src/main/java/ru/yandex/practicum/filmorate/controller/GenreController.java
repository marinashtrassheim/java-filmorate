package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreDbStorage genreStorage;

    public GenreController(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping
    public Collection<Genre> getAllGenre() {
        return genreStorage.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        return  genreStorage.getGenre(id);
    }
}
