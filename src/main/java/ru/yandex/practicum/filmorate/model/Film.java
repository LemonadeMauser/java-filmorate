package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Set<Integer> usersLikes = new HashSet<>();
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Некорректное название фильма")
    @Size(max = 60, message = "Название фильма превышает лимит (60 символов)")
    private final String name;
    @NotNull(message = "Добавьте описание фильма")
    @Size(min = 1, max = 200, message = "Описание превышает максимальный размер (200 символов)")
    private final String description;
    private final LocalDate releaseDate;
    @Min(value = 1, message = "Некорректно указана продолжительность фильма")
    private final long duration;

}
