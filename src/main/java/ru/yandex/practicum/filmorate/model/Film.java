package ru.yandex.practicum.filmorate.model;


import lombok.*;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.NotBefore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */

@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;
    @NotBefore(value = "1895-12-28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;
    private Set<Integer> userLikes = new HashSet<>();

    public void addLike(int userId) {
        userLikes.add(userId);
    }

    public void deleteLike(int userId) {
        userLikes.remove(userId);
    }

    public boolean hasLike(int userId) {
        return userLikes.contains(userId);
    }

    public int getLikesCount() {
        return userLikes.size();
    }
}
