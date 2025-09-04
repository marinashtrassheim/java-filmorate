package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    private UserService userService;
    private InMemoryUserStorage userStorage;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

        // Создаем тестовых пользователей
        user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User One");

        user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User Two");

        user3 = new User();
        user3.setEmail("user3@mail.ru");
        user3.setLogin("user3");
        user3.setName("User Three");

        // Добавляем пользователей в хранилище
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
    }

    @AfterEach
    void tearDown() {
        userStorage.users.clear();
    }

    @Test
    void addFriend_ShouldAddFriendsToBothUsers() {
        userService.addFriend(user1.getId(), user2.getId());

        User updatedUser1 = userStorage.getUser(user1.getId());
        User updatedUser2 = userStorage.getUser(user2.getId());

        assertTrue(updatedUser1.hasFriend(user2.getId()), "Друг должен быть добавлен к пользователю");
        assertTrue(updatedUser2.hasFriend(user1.getId()), "Пользователь должен быть добавлен к другу");
    }

    @Test
    void removeFriend_ShouldRemoveFriendsFromBothUsers() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());

        User updatedUser1 = userStorage.getUser(user1.getId());
        User updatedUser2 = userStorage.getUser(user2.getId());

        assertFalse(updatedUser1.hasFriend(user2.getId()), "Друг должен быть удален у пользователя");
        assertFalse(updatedUser2.hasFriend(user1.getId()), "Пользователь должен быть удален у друга");
    }

    @Test
    void friendsOverlap_ShouldReturnCommonFriends() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        Collection<User> commonFriends = userService.friendsOverlap(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size(), "Должен быть один общий друг (user3)");
        assertTrue(commonFriends.stream().anyMatch(u -> u.getId() == user3.getId()),
                "Общий друг user3 должен быть в результате");
    }

}
