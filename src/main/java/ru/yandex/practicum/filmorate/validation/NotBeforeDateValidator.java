package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NotBeforeDateValidator implements ConstraintValidator<NotBefore, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(NotBefore constraintAnnotation) {
        this.minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // или false, в зависимости от требований
        }
        return !value.isBefore(minDate);
    }
}