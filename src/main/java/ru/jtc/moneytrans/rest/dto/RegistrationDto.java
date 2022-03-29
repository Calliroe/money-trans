package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;

@Data
public class RegistrationDto {
    @Size(min = 2, max = 50)
    @NotBlank
    private String username;
    @Size(min = 6, max = 12)
    @NotBlank
    private String password;
}
