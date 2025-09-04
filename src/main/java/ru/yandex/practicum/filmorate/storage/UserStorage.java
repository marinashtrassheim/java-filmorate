package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> getUsers();
    User getUser(int userId);
    User createUser(User user);
    User updateUser(User user);
    boolean isUserExists(int userId);
    Set<Integer> getUserFriends(int userId);

}
