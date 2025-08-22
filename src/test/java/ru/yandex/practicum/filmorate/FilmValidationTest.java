package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmValidationTest {
    @Autowired
    private Validator validator;

    private Film validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
    }
    @Test
    void whenAllFieldsValid_thenNoViolations() {
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertEquals(0, violations.size(), "Не должно быть нарушений валидации");
    }

    @Test
    void whenNameIsBlank_thenOneViolation() {
        validFilm.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameIsNull_thenOneViolation() {
        validFilm.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionTooLong_thenOneViolation() {
        String longDescription = "A".repeat(201); // 201 символ
        validFilm.setDescription(longDescription);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания - 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionExactly200Chars_thenNoViolations() {
        String exactLengthDescription = "A".repeat(200); // 200 символов
        validFilm.setDescription(exactLengthDescription);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(0, violations.size());
    }

    @Test
    void whenReleaseDateBefore1895_thenOneViolation() {
        validFilm.setReleaseDate(LocalDate.of(1890, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(1, violations.size());
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }

    @Test
    void whenReleaseDateExactly18951228_thenNoViolations() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(0, violations.size());
    }

    @Test
    void whenReleaseDateAfter1895_thenNoViolations() {
        validFilm.setReleaseDate(LocalDate.of(1896, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(0, violations.size());
    }

    @Test
    void whenDurationIsZero_thenOneViolation() {
        validFilm.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void whenDurationIsNegative_thenOneViolation() {
        validFilm.setDuration(-10);
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void whenDurationIsPositive_thenNoViolations() {
        validFilm.setDuration(1); // минимальное положительное
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(0, violations.size());
    }

    @Test
    void whenMultipleViolations_thenAllReported() {
        validFilm.setName("");
        validFilm.setDescription("A".repeat(201));
        validFilm.setReleaseDate(LocalDate.of(1890, 1, 1));
        validFilm.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertEquals(4, violations.size());

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());

        assertTrue(messages.contains("Название не может быть пустым"));
        assertTrue(messages.contains("Максимальная длина описания - 200 символов"));
        assertTrue(messages.contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
        assertTrue(messages.contains("Продолжительность фильма должна быть положительным числом"));
    }

}
