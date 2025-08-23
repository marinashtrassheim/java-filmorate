package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class User {
    private int id;
    @Email(message = "Email должен быть корректным адресом")
    private String email;
    @NotBlank(message = "Не верный формат логина")
    @Pattern(regexp = "\\S+", message = "Не верный формат логина")
    private String login;
    private String name;
    private LocalDate birthday;

    @AssertTrue(message = "Дата рождения не может быть в будущем")
    public boolean isBirthdayValid() {
        if (birthday == null) {
            return true; // если дата не указана - пропускаем
        }
        return !birthday.isAfter(LocalDate.now());
    }

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}
