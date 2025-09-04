package ru.yandex.practicum.filmorate.exception;

public class CanNotAddLikeException extends RuntimeException {
    public CanNotAddLikeException(String message) {
        super(message);
    }
}
