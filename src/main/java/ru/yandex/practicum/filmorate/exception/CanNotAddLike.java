package ru.yandex.practicum.filmorate.exception;

public class CanNotAddLike extends RuntimeException {
    public CanNotAddLike(String message) {
        super(message);
    }
}
