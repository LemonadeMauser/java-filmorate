package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<User> findAll() {
        log.info("Список пользователей отправлен");

        return userDbStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        log.info("Пользователь добавлен");

        return userDbStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        if (getById(user.getId()).isEmpty()) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь {} обновлен", user.getId());

        return userDbStorage.update(user);
    }

    public Optional<User> getById(int id) {
        Optional<User> user = userDbStorage.getById(id);
        if (user.isEmpty()) {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь с id {} отправлен", id);

        return userDbStorage.getById(id);
    }

    public Optional<User> deleteById(int id) {
        if (getById(id).isEmpty()) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь {} удален", id);

        return userDbStorage.deleteById(id);
    }

    public List<Integer> followUser(int followingId, int followerId) {
        if (getById(followingId).isEmpty() || getById(followerId).isEmpty()) {
            log.warn("Пользователи с id {} и(или) {} не найден(ы)", followingId, followerId);
            throw new ValidationException("Пользователи не найдены");
        }
        String checkFriendship = "SELECT * FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkFriendship, followingId, followerId);
        if (userRows.first()) {
            log.warn("Пользователь уже подписан");
            throw new ValidationException("Пользователь уже подписан");
        }
        log.info("Пользователь {} подписался на {}", followingId, followerId);

        return userDbStorage.followUser(followingId, followerId);
    }

    public List<Integer> unfollowUser(int followingId, int followerId) {
        String checkFriendship = "SELECT * FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkFriendship, followingId, followerId);
        if (!userRows.first()) {
            log.warn("Пользователь не подписан");
            throw new ValidationException("Пользователь не подписан");
        }
        log.info("Пользователь {} отписался от {}", followerId, followingId);

        return userDbStorage.unfollowUser(followingId, followerId);
    }

    public List<User> getFriendsListById(int id) {
        if (getById(id).isEmpty()) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Запрос получения списка друзей пользователя {} выполнен", id);

        return userDbStorage.getFriendsListById(id);
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        if (getById(firstId).isEmpty() || getById(secondId).isEmpty()) {
            log.warn("Пользователи с id {} и {} не найдены", firstId, secondId);
            throw new NotFoundException("Пользователи не найдены");
        }
        log.info("Список общих друзей {} и {} отправлен", firstId, secondId);

        return userDbStorage.getCommonFriendsList(firstId, secondId);
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }
}