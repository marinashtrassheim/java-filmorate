package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: ID={}, email={}, login={}",
                user.getId(), user.getEmail(), user.getLogin());
        return user;
    }

    @PutMapping("/{id}")
    public User updateUser(@Valid @RequestBody User newUser, @PathVariable int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        newUser.setId(id);
        users.put(id, newUser);
        log.info("Пользователь изменен: ID={}, email={}, login={}",
                newUser.getId(), newUser.getEmail(), newUser.getLogin());
        return newUser;
    }

    private int getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }
}
