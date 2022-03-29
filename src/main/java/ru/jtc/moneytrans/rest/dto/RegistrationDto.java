package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;

@Data
public class RegistrationDto {
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов")
    @NotBlank(message = "Введите имя пользователя")
    private final String username;
    @Size(min = 4, max = 12, message = "Пароль должен содержать от 4 до 12 символов")
    @NotBlank(message = "Введите пароль")
    private final String password;
}
