package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CanNotBeAddedAsFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage")UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        if (!userStorage.isUserExists(friendId)) {
            throw new NotFoundException("Пользователя с id: " + friendId + " не существует");
        }

        if (userId == friendId) {
            throw new CanNotBeAddedAsFriendException("Нельзя добавлять в друзья самого пользователя");
        }
        User user = userStorage.getUser(userId);

        if (user.hasFriend(friendId)) {
            throw new CanNotBeAddedAsFriendException("Пользователя с id: " + friendId + " уже есть в друзьях");
        }

        userStorage.addUserFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        if (!userStorage.isUserExists(friendId)) {
            throw new NotFoundException("Пользователя с id: " + friendId + " не существует");
        }
       userStorage.removeUserFriend(userId, friendId);
    }

    public Collection<User> getFriendsDetails(int userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        Set<Integer> friendsId = userStorage.getUserFriends(userId);

        return friendsId.stream()
                .map(userStorage::getUser) // получаем User по ID
                .filter(Objects::nonNull)  // фильтруем null (на всякий случай)
                .collect(Collectors.toList());
    }

    public Collection<User> friendsOverlap(int userId, int otherUserId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        if (!userStorage.isUserExists(otherUserId)) {
            throw new NotFoundException("Пользователя с id: " + otherUserId + " не существует");
        }

        if (userId == otherUserId) {
            throw new CanNotBeAddedAsFriendException("Требуется указать разных пользователей");
        }

        Collection<User> userFriends = getFriendsDetails(userId);
        Collection<User> otherUserFriends = getFriendsDetails(otherUserId);

        return userFriends.stream()
                .filter(otherUserFriends::contains) // только те, кто есть в обоих списках
                .collect(Collectors.toList());
    }

}
