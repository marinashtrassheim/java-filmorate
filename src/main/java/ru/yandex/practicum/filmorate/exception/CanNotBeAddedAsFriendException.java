package ru.yandex.practicum.filmorate.exception;

public class CanNotBeAddedAsFriendException extends RuntimeException {
    public CanNotBeAddedAsFriendException(String message) {
        super(message);
    }
}
