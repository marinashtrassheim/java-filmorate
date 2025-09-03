package ru.yandex.practicum.filmorate.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.CanNotAddLike;
import ru.yandex.practicum.filmorate.exception.CanNotBeAddedAsFriendException;
import ru.yandex.practicum.filmorate.exception.CanNotBeRemovedFromFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getDefaultMessage(); // Берем только сообщение для пользователя
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; ")); // Объединяем все ошибки через точку с запятой

        return new ErrorResponse("Bad Request", errorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse("Not found", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(
            ConstraintViolationException e) {
        // Извлекаем только пользовательские сообщения
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage) // Берем только сообщение для пользователя
                .collect(Collectors.joining("; "));

        return new ErrorResponse("Bad Request", errorMessage);
    }

    @ExceptionHandler(CanNotAddLike.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCanNotAddLikeException(CanNotAddLike e) {
        return new ErrorResponse("Bad Request", e.getMessage());
    }

    @ExceptionHandler(CanNotBeAddedAsFriendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCanNotBeAddedAsFriendExceptionException(CanNotBeAddedAsFriendException e) {
        return new ErrorResponse("Bad Request", e.getMessage());
    }

    @ExceptionHandler(CanNotBeRemovedFromFriendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCanNotBeRemovedFromFriendExceptionException(CanNotBeRemovedFromFriendException e) {
        return new ErrorResponse("Bad Request", e.getMessage());
    }
}