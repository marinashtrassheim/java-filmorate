package ru.yandex.practicum.filmorate.exception;

public class CanNotBeRemovedFromFriendException extends RuntimeException {
    public CanNotBeRemovedFromFriendException(String message) {
        super(message);
    }
}
