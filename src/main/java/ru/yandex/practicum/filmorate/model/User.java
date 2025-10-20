package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Integer> requestFriends = new HashSet<>();
    private Set<Integer> friends = new HashSet<>();

    @AssertTrue(message = "Дата рождения не может быть в будущем")
    @JsonIgnore
    public boolean isBirthdayValid() {
        if (birthday == null) {
            return true;
        }
        return !birthday.isAfter(LocalDate.now());
    }

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public boolean hasFriend(int friendId) {
        return friends.contains(friendId);
    }

    public void addRequestFriends(int friendId) {
        requestFriends.add(friendId);
    }

}
