package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int id;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) throws ValidationException {
        checkIsUserDataCorrect(newUser);
        String name = newUser.getName();
        String login = newUser.getLogin();
        if (name == null || name.isEmpty()) {
            newUser.setName(login);
            log.debug("Для пользователя с логином {} установлено новое имя {}", login, newUser.getName());
        }
        newUser.setId(++this.id);
        users.put(newUser.getId(), newUser);
        log.info("Добавлен новый пользователь id={}", newUser.getId());
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) throws ValidationException {
        checkIsUserDataCorrect(newUser);
            if (!users.containsKey(newUser.getId())) {
                throw new ValidationException("Ошибка - пользователь id=" + newUser.getId() + " не найден");
            } else {
                users.put(newUser.getId(), newUser);
                log.info("Данные пользователя id = {} обновлены", newUser.getId());
            }

        return newUser;
    }

    public boolean check(User newUser) throws ValidationException {
        if (newUser.getEmail() == null || (!newUser.getEmail().contains("@")) || newUser.getEmail().isBlank()) {
            log.info("Ошибка - некорректно указан email");
            throw new ValidationException("Некорректный email");
        } else if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.info("Ошибка - некорректно указан login");
            throw new ValidationException("Некорректно указан login");
        } else if (newUser.getBirthday() == null || getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
            log.info("Ошибка - некорректно указана дата рождения");
            throw new ValidationException("Некорректно указана дата рождения");
        } else if (newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            return true;
        } else {
            return true;
        }
    }

    public static void checkIsUserDataCorrect(User newUser) throws ValidationException {
        String email = newUser.getEmail();
        String login = newUser.getLogin();
        LocalDate birthday = LocalDate.parse(newUser.getBirthday());
        if (email == null || email.isEmpty() || !email.contains("@")) {
            log.debug("Электронная почта не указана или не указан символ '@'");
            throw new ValidationException("Указан некорректный email");
        } else if (login == null || login.isEmpty() || login.contains(" ")) {
            log.debug("Логин пользователя с электронной почтой {} не указан или содержит пробел", email);
            throw new ValidationException("Некорректно указан login");
        } else if (birthday.isAfter(LocalDate.now())) {
            log.debug("Дата рождения пользователя с логином {} указана будущим числом", login);
            throw new ValidationException("Некорректно указана дата рождения");
        }
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }
}
