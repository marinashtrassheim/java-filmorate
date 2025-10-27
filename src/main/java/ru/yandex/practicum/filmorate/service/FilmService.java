package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CanNotAddLikeException;
import ru.yandex.practicum.filmorate.exception.CanNotDeleteLikeException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    public final FilmStorage filmStorage;
    public final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film setLike(int filmId, int userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        if (!filmStorage.isFilmExists(filmId)) {
            throw new NotFoundException("Фильма с id: " + filmId + " не существует");
        }

        Film filmToSetLike = filmStorage.getFilm(filmId);

        if (filmToSetLike.hasLike(userId)) {
            throw new CanNotAddLikeException("Пользователя с id: " + userId + " уже ставил лайк");
        }

        filmStorage.addLike(filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public Film deleteLike(int filmId, int userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        if (!filmStorage.isFilmExists(filmId)) {
            throw new NotFoundException("Фильма с id: " + filmId + " не существует");
        }

        Film filmToDeleteLike = filmStorage.getFilm(filmId);

        if (!filmToDeleteLike.hasLike(userId)) {
            throw new CanNotDeleteLikeException("Пользователь с id: " + userId + " не ставил лайк этому фильму");
        }

        filmStorage.removeLike(filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public Collection<Film> topFilms(int count) {
        List<Film> sortedFilms =  filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((Film film) ->
                        film.getUserLikes().size()).reversed())
                .toList();
        int actualCount = Math.min(count, sortedFilms.size());
        return sortedFilms.subList(0, actualCount);
    }
}
