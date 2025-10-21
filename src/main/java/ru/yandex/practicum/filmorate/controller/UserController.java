package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(@Qualifier("userDbStorage") UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userStorage.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable("id") int userId) {
        return userService.getFriendsDetails(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getFriendsOverlap(@PathVariable int id, @PathVariable int otherId) {
        return userService.friendsOverlap(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        User createdUser = userStorage.createUser(user);
        log.info("Пользователь создан: ID={}, email={}, login={}",
                createdUser.getId(), createdUser.getEmail(), createdUser.getLogin());
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        User updatedUser = userStorage.updateUser(newUser);
        log.info("Пользователь изменен: ID={}, email={}, login={}",
                updatedUser.getId(), updatedUser.getEmail(), updatedUser.getLogin());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        log.info("Друг ID={} добавлен к пользователю ID={}",
                id, friendId);
        return userStorage.getUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
        log.info("Друг ID={} удален из списка друзей пользователя ID={}",
                id, friendId);
        return  userStorage.getUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public User confirmFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.confirmFriend(id, friendId);
        log.info("Дружба подтверждена между {} и {}", id, friendId);
        return userStorage.getUser(id);
    }

}
