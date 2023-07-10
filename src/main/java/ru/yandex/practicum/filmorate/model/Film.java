package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @PositiveOrZero
    private int id;
    @NotBlank(message = "Некорректное название фильма")
    @Size(max = 60, message = "Название фильма превышает лимит (60 символов)")
    private String name;
    @NotNull(message = "Добавьте описание фильма")
    @Size(min = 1, max = 200, message = "Описание превышает максимальный размер (200 символов)")
    private String description;
    private LocalDate releaseDate;
    @Min(value = 1, message = "Некорректно указана продолжительность фильма")
    private long duration;
    private Mpa mpa;
    private List<Genre> genres;
}
