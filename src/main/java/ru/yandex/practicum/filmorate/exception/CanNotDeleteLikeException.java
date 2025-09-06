package ru.yandex.practicum.filmorate.exception;

public class CanNotDeleteLikeException extends RuntimeException {
    public CanNotDeleteLikeException(String message) {
        super(message);
    }
}
