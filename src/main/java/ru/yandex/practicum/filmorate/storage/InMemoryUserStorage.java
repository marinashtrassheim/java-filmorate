package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    public Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() { return users.values(); }

    @Override
    public User getUser(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
        }
        newUser.setId(newUser.getId());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public boolean userExists(int userId) {
        return !users.containsKey(userId);
    }

    @Override
    public Set<Integer> getUserFriends(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        return user.getFriends();
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
