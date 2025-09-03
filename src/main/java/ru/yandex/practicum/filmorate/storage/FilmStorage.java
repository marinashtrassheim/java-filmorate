package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film createFilm(Film film);
    Film updateFilm(Film newFilm);
    Collection<Film> getFilms();
    Film getFilm(int filmId);
    boolean filmExists(int filmId);
}
