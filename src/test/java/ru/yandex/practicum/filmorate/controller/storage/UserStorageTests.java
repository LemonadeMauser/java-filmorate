package ru.yandex.practicum.filmorate.controller.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStorageTests {

    private InMemoryUserStorage userStorage;
    private User user;

    @BeforeEach
    void init() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void userWithoutNameTest() {
        user = new User("lema@mail.ru", "lema", LocalDate.of(1990, 5, 6));
        userStorage.validate(user);
        assertEquals("lema", user.getName());

        user = new User("lema@mail.ru", "lema", LocalDate.of(1990, 5, 6));
        user.setName(" ");
        userStorage.validate(user);
        assertEquals("lema", user.getName());
    }

    @Test
    void duplicateUserTest() {
        user = new User("lema@mail.ru", "lema", LocalDate.of(1990, 5, 6));
        user.setId(1);
        userStorage.getUsers().put(user.getId(), user);
        assertThrows(Throwable.class, () -> userStorage.checkUsers(user));
    }
}
