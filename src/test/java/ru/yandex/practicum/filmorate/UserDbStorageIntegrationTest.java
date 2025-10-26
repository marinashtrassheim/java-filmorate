package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageIntegrationTest {

    private final UserDbStorage userStorage;
    private User user101;
    private User user102;
    private User user103;

    @BeforeEach
    void setUp() {

        user101 = new User();
        user101.setEmail("user101@mail.ru");
        user101.setLogin("user101");
        user101.setName("User One");

        user102 = new User();
        user102.setEmail("user102@mail.ru");
        user102.setLogin("user102");
        user102.setName("User Two");

        user103 = new User();
        user103.setEmail("user103@mail.ru");
        user103.setLogin("user103");
        user103.setName("User Three");

        userStorage.createUser(user101);
        userStorage.createUser(user102);
        userStorage.createUser(user103);
    }

    @Test
    void createUser() {
        User userFromDb = userStorage.getUser(user101.getId());
        assertThat(userFromDb.getEmail()).isEqualTo("user101@mail.ru");
        assertThat(userFromDb.getLogin()).isEqualTo("user101");
        assertThat(userFromDb.getName()).isEqualTo("User One");
    }

    @Test
    void updateUser() {
        User existingUser = userStorage.getUser(user101.getId());
        existingUser.setEmail("updated@mail.ru");
        existingUser.setLogin("updated_login");
        existingUser.setName("Updated Name");

        userStorage.updateUser(existingUser);

        User userFromDb = userStorage.getUser(user101.getId());
        assertThat(userFromDb.getEmail()).isEqualTo("updated@mail.ru");
        assertThat(userFromDb.getLogin()).isEqualTo("updated_login");
        assertThat(userFromDb.getName()).isEqualTo("Updated Name");

    }

    @Test
    void addFriend_ShouldAddFriendsToBothUsers() {
        userStorage.addUserFriend(user101.getId(), user102.getId());

        User updateduser101 = userStorage.getUser(user101.getId());
        User updateduser102 = userStorage.getUser(user102.getId());

        assertTrue(updateduser101.hasFriend(user102.getId()), "Друг должен быть добавлен к пользователю");
        assertTrue(updateduser102.hasFriend(user101.getId()), "Пользователь должен быть добавлен к другу");
    }

    @Test
    void removeFriend_ShouldRemoveFriendsFromBothUsers() {
        userStorage.addUserFriend(user101.getId(), user102.getId());
        userStorage.removeUserFriend(user101.getId(), user102.getId());

        User updateduser101 = userStorage.getUser(user101.getId());
        User updateduser102 = userStorage.getUser(user102.getId());

        assertFalse(updateduser101.hasFriend(user102.getId()), "Друг должен быть удален у пользователя");
        assertFalse(updateduser102.hasFriend(user101.getId()), "Пользователь должен быть удален у друга");
    }
}

