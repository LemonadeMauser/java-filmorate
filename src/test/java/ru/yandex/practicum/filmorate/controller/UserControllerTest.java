package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    static UserController userController;
    static User testUser;

    @BeforeAll
    static void beforeAll() {
        userController = new UserController();
    }

    @BeforeEach
    void beforeEach() {
        testUser = new User();
        testUser.setName("Juan");
        testUser.setLogin("TestJuan");
        testUser.setBirthday("2008-08-08");
        testUser.setEmail("juan@spok.ar");
    }

    @Test
    void shouldApproveUserWithCorrectData() throws ValidationException {
        assertTrue(userController.check(testUser),
                "Error");
    }

    @Test
    void shouldDeclineUserWithIncorrectEmail() {
        testUser.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail("");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail(" ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail("juan@spok.ar");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    void shouldDeclineUserWithIncorrectLogin() {
        testUser.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin("");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin(" ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin("Juan ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    void shouldDeclineUserWithIncorrectBirthDay() {
        testUser.setBirthday("20400-08-08");
        Throwable thrown = catchThrowable(() -> {
            userController.addUser(testUser);
        });
        assertThat(thrown).isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldReplaceEmptyNameByLogin() throws ValidationException {
        testUser.setName("CorrectLogin");
        userController.checkIsUserDataCorrect(testUser);
        assertEquals("CorrectLogin", testUser.getName());
    }
}