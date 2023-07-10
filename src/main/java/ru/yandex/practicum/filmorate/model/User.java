package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @PositiveOrZero
    private int id;
    @NotBlank(message = "Поле email - пустое")
    @Email(message = "Некорректный email")
    @Size(max = 50)
    private String email;
    @NotNull(message = "Поле логин - пустое")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    @Size(min = 1, max = 20)
    private String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Указанна не корректная дата рождения")
    private LocalDate birthday;
}

