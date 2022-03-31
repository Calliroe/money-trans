package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AccountInfo {

    @Size(min = 6, max = 6, message = "Номер счёта должен содержать 6 символов")
    @NotBlank(message = "Введите номер счёта")
    private String accountNumber;
    @NotNull(message = "Введите БИК")
    private Long bic;
    private Double balance;

}
