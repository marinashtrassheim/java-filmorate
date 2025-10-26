package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.CanNotBeAddedAsFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final UserRowMapper userRowMapper;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(UserRowMapper userRowMapper, JdbcTemplate jdbcTemplate) {
        this.userRowMapper = userRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT id, " +
                "email, " +
                "login, " +
                "name, " +
                "birthday " +
                "FROM users ";
        Collection<User> users = jdbcTemplate.query(sql, userRowMapper);
        for (User user : users) {
            user.setFriends(getUserFriends(user.getId()));
        }
        return users;
    }

    @Override
    public User getUser(int userId) {
        String sql = "SELECT id, " +
                "email, " +
                "login, " +
                "name, " +
                "birthday " +
                "FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, userId);
            if (user != null) {
                user.setFriends(getUserFriends(userId));
            }
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null);
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?," +
                "login = ?," +
                "name = ?," +
                "birthday = ?" +
                "WHERE id = ?";
        if (!isUserExists(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null,
                user.getId());
        return user;
    }

    @Override
    public boolean isUserExists(int userId) {
        String sql = "SELECT id FROM users WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Integer.class, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Set<Integer> getUserFriends(int userId) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ? " +
                "UNION " +
                "SELECT user_id FROM friendship WHERE friend_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, userId, userId));
    }

    @Override
    public void addUserFriend(int userId, int friendId) {
        if (isFriendshipExists(userId, friendId)) {
            throw new CanNotBeAddedAsFriendException("Дружба уже существует");
        }
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Создано записей дружбы: {}->{}",
                userId, friendId);
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendship f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
                "JOIN friendship f2 ON u.id = f2.friend_id AND f2.user_id = ? ";
        return jdbcTemplate.query(sql, userRowMapper, userId1, userId2);
    }

    @Override
    public void removeUserFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Удалено записей дружбы: {}->{}",
                userId, friendId);
    }

    private boolean isFriendshipExists(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count > 0;
    }
}
