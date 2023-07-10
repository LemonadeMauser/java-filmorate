package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTests {
    private Film film;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void rightFieldsValidationTest() {
        film = new Film(1, "Film", "Something about good story", LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullNameValidationTest() {
        film = new Film(1, null, "Something about good story", LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void blankNameValidationTest() {
        film = new Film(1, " ", "Something", LocalDate.of(1896, 1, 2), 60, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void nullDescriptionTest() {
        film = new Film(1, "Film", null, LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyDescriptionTest() {
        film = new Film(1, "Film", "", LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void descriptionBiggerThen200Test() {
        film = new Film(1, "Film", "Жмурки es una película rusa de comedia y crimen dirigida por" +
                "Aleksey Balabanov. Ambientada en la década de 1990, la historia sigue a cuatro amigos que se dedican" +
                "al negocio ilegal de la venta de armas de fuego. Con un enfoque satírico, la película aborda temas" +
                "como la corrupción, la violencia y la amistad. A medida que se ven envueltos en situaciones" +
                "peligrosas, su lealtad y confianza son puestas a prueba. Con una combinación de humor negro" +
                "y un estilo visual distintivo, Жмурки ofrece una mirada sarcástica a la sociedad rusa de la época." +
                "¿Podrán estos personajes hilarantes y despiadados sobrevivir a su turbulento mundo criminal?" +
                "Descúbrelo en esta aclamada película.",
                LocalDate.of(2021, 1, 2), 120, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void durationLessThen1MinuteTest() {
        film = new Film(1, "Film", "Something about good story", LocalDate.of(2021, 1, 2), 0, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}
