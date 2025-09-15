package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBeforeDateValidator.class)
public @interface NotBefore {
    String message() default "Дата не может быть раньше указанной";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value(); // Дата в формате "yyyy-MM-dd"
}
