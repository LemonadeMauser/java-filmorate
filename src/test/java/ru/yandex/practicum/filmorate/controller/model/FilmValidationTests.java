package ru.yandex.practicum.filmorate.controller.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

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
    Film film;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void rightFieldsValidationTest() {
        film = new Film("pelicula", "aqui tiene una buena historia", LocalDate.of(2021, 1, 2), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullNameValidationTest() {
        film = new Film(null, "aqui tiene una buena historia", LocalDate.of(2021, 1, 2), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void blankNameValidationTest() {
        film = new Film(" ", "aqui tiene una buena historia", LocalDate.of(1896, 1, 2), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void nullDescriptionTest() {
        film = new Film("pelicula", null, LocalDate.of(2021, 1, 2), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyDescriptionTest() {
        film = new Film("pelicula", "", LocalDate.of(2021, 1, 2), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void descriptionBiggerThen200Test() {
        film = new Film("pelicula", "rerererrewrerwerwerwerwfsdfcjadskplfasdfbsahdfjnklsdfjhjagshvdfbjs" +
                "Sdasdshgdsgdhjaksdhaghfdahjskajdhaugysdftaghjhiuygtfrdetfyjguhijuhgyfttdsuiofjasdfijksadhfsakdffsd" +
                "dfghjkljhgvhdabsjdashjdabdashdjasdlsjkdfsfnjskfnsafjkmnvdjfsdnfsjdfasdbnfasdhjfbasdfaksjfsadjfbsdf" +
                "dasdasdadsasdasdasdadsadasdfsdfsfsdfsdfsdfsdfasdf"
                , LocalDate.of(2021, 1, 2), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void durationLessThen1MinuteTest() {
        film = new Film("pelicula", "aqui tiene una buena historia", LocalDate.of(2021, 1, 2), 0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}
