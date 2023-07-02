package ru.yandex.practicum.filmorate.controller.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTests {
    private User user;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void rightFieldsValidationTest() {
        user = new User("lema@mail.ru", "lema", LocalDate.of(1999, 8, 10));
        user.setName("Sergio");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void wrongEmailValidationTest() {
        user = new User("lema.ru", "lema", LocalDate.of(1999, 8, 10));
        user.setName("Sergio");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyEmailValidationTest() {
        user = new User(null, "lema", LocalDate.of(1999, 8, 10));
        user.setName("Sergio");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullLoginValidationTest() {
        user = new User("lema@mail.ru", null, LocalDate.of(1999, 8, 10));
        user.setName("Sergio");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyLoginValidationTest() {
        user = new User("lema@mail.ru", "", LocalDate.of(1999, 8, 10));
        user.setName("Sergip");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void loginWithSpacesValidationTest() {
        user = new User("lema@mail.ru", "l ema", LocalDate.of(1999, 8, 10));
        user.setName("Sergio");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void withoutBirthdayValidationTest() {
        user = new User("lema@mail.ru", "lema", null);
        user.setName("Sergio");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}
