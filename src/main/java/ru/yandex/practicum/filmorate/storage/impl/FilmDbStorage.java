package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";

        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        String mpaSql = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
        if (film.getMpa() != null) {
            jdbcTemplate.update(mpaSql, film.getId(), film.getMpa().getId());
            film.setMpa(findMpa(film.getId()));
        }
        String genresSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(g -> jdbcTemplate.update(genresSql, film.getId(), g.getId()))
                    .collect(Collectors.toSet());
            film.setGenres(findGenres(film.getId()));
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        if (film.getMpa() != null) {
            String deleteMpa = "DELETE FROM mpa_films WHERE film_id = ?";
            String updateMpa = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteMpa, film.getId());
            jdbcTemplate.update(updateMpa, film.getId(), film.getMpa().getId());
            film.setMpa(findMpa(film.getId()));
        }
        if (film.getGenres() != null) {
            String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
            String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            Set<Integer> existingGenreIds = new HashSet<>();

            jdbcTemplate.update(deleteGenres, film.getId());

            for (Genre g : film.getGenres()) {
                existingGenreIds.add(g.getId());
            }

            for (Integer genreId : existingGenreIds) {
                String checkDuplicate = "SELECT COUNT(*) FROM film_genre WHERE film_id = ? AND genre_id = ?";
                int count = jdbcTemplate.queryForObject(checkDuplicate, Integer.class, film.getId(), genreId);
                if (count == 0) {
                    jdbcTemplate.update(updateGenres, film.getId(), genreId);
                }
            }

            film.setGenres(findGenres(film.getId()));
        }
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());

        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!filmRows.next()) {
            return Optional.empty();
        }

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeFilm, id));
    }

    @Override
    public Optional<Film> deleteById(int id) {
        Optional<Film> film = getById(id);
        String genresSql = "DELETE FROM film_genre WHERE film_id = ?";
        String mpaSql = "DELETE FROM mpa_films WHERE film_id = ?";
        jdbcTemplate.update(genresSql, id);
        jdbcTemplate.update(mpaSql, id);
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);

        return film;
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);

        return getById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int filmId, int userId) {
        String sql = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);

        return getById(filmId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String sql = "SELECT id, name, description, release_date, duration " +
                "FROM films " +
                "LEFT JOIN films_likes fl ON films.id = fl.film_id " +
                "group by films.id, fl.film_id IN ( " +
                "    SELECT film_id " +
                "    FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");

        return new Film(id, name, description, releaseDate, duration, findMpa(id), findGenres(id));
    }

    private List<Genre> findGenres(int filmId) {
        String genresSql = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN FILM_GENRE FG on genre.genre_id = FG.GENRE_ID " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(genresSql, genreDbStorage::makeGenre, filmId);
    }

    private Mpa findMpa(int filmId) {
        String mpaSql = "SELECT id, name " +
                "FROM mpa " +
                "LEFT JOIN MPA_FILMS MF ON mpa.id = mf.mpa_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.queryForObject(mpaSql, mpaDbStorage::makeMpa, filmId);
    }
}