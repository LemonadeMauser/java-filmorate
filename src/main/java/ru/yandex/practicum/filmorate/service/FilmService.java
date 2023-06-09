package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public Collection<Film> findAll() {
        log.info("Список фильмов отправлен");

        return filmDbStorage.findAll();
    }

    public Film create(Film film) {
        validate(film);
        log.info("Фильм добавлен");

        return filmDbStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);
        if (getById(film.getId()).isEmpty()) {
            log.warn("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Фильм {} обновлен", film.getId());

        return filmDbStorage.update(film);
    }

    public Optional<Film> getById(int id) {
        Optional<Film> film = filmDbStorage.getById(id);
        if (film.isEmpty()) {
            log.warn("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Фильм с id {} отправлен", id);

        return filmDbStorage.getById(id);
    }

    public Optional<Film> deleteById(int id) {
        if (getById(id).isEmpty()) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Фильм {} удален", id);

        return filmDbStorage.deleteById(id);
    }

    public Optional<Film> addLike(int filmId, int userId) {
        if (filmDbStorage.getById(filmId).isEmpty() || userDbStorage.getById(userId).isEmpty()) {
            log.warn("Фильм {} и(или) пользователь {} не найден.", filmId, userId);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);

        return filmDbStorage.addLike(filmId, userId);
    }

    public Optional<Film> removeLike(int filmId, int userId) {
        if (filmDbStorage.getById(filmId).isEmpty() || userDbStorage.getById(userId).isEmpty()) {
            log.warn("Фильм {} и(или) пользователь {} не найден.", filmId, userId);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);

        return filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getBestFilms(int count) {
        log.info("Отправлен список из {} самых популярных фильмов", count);

        return filmDbStorage.getBestFilms(count);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE))
            throw new ValidationException("В то время кино еще не было");
    }
}