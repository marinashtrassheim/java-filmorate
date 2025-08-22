package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserValidationTest {
    @Autowired
    private Validator validator;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setEmail("valid@email.com");
        validUser.setLogin("Valid_login");
        validUser.setName("Valid_name");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertEquals(0, violations.size(), "Не должно быть нарушений валидации");
    }

    @Test
    void  whenEmailIsWrong_thenOneViolation() {
        validUser.setEmail("wrongEmail");
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertEquals(1, violations.size());
        assertEquals("Email должен быть корректным адресом", violations.iterator().next().getMessage());
    }


    @Test
    void whenLoginIsBlank_thenOneViolation() {
        validUser.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(2, violations.size());
        assertEquals("Поле не должно содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void whenLoginIsNull_thenOneViolation() {
        validUser.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(1, violations.size());
        assertEquals("Поле не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameIsBlank_thenNoViolations() {
        validUser.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(0, violations.size());
        assertEquals(validUser.getName(), validUser.getLogin());
    }

    @Test
    void whenNameIsNull_thenNoViolations() {
        validUser.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(0, violations.size());
        assertEquals(validUser.getName(), validUser.getLogin());
    }

    @Test
    void whenBirthDateFromTheFuture_thenOneViolation() {
        validUser.setBirthday(LocalDate.of(3000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void whenMultipleViolations_thenAllReported() {
        validUser.setLogin(null);
        validUser.setBirthday(LocalDate.of(3000, 1, 1));
        validUser.setEmail("wrongEmail");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertEquals(3, violations.size());


        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());

        assertTrue(messages.contains("Поле не может быть пустым"));
        assertTrue(messages.contains("Дата рождения не может быть в будущем"));
        assertTrue(messages.contains("Email должен быть корректным адресом"));
    }

}
