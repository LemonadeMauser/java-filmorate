package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Instant minRealeaseDate = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));
    private int id;

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) throws ValidationException {
        if (checkIsFilmDataCorrect(newFilm)) {
            films.put(newFilm.getId(), newFilm);
            newFilm.setId(++this.id);
            log.info("Добавлен новый фильм id={}", newFilm.getId());
        }
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) throws ValidationException {
        if (checkIsFilmDataCorrect(newFilm)) {
            if (films.containsKey(newFilm.getId())) {
                films.put(newFilm.getId(), newFilm);
                log.info("Обновлены данные о фильме id={}", newFilm.getId());
            } else {
                throw new ValidationException("Ошибка - фильм не найден");
            }
        }
        return newFilm;
    }

    public boolean checkIsFilmDataCorrect(Film newFilm) throws ValidationException {
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.info("Ошибка - не указано название");
            throw new ValidationException("Не указано название фильма");
        } else if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
            log.info("Ошибка - превышена допустимая длина описания");
            throw new ValidationException("Превышен лимит в 200 символов");
        } else if (newFilm.getReleaseDate() == null || getInstance(newFilm.getReleaseDate())
                .isBefore(minRealeaseDate)) {
            log.info("Ошибка - указана некорректная дата выхода");
            throw new ValidationException("Некорректная дата выхода фильма");
        } else if (getDuration(newFilm.getDuration()).isNegative() || getDuration(newFilm.getDuration()).isZero()) {
            log.info("Ошибка - некорректно указана длительность");
            throw new ValidationException("Некорректная длительность фильма");
        } else {
            return true;
        }
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }

    private Duration getDuration(long duration) {
        return Duration.ofMinutes(duration);
    }
}
