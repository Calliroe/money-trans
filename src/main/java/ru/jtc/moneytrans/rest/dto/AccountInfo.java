package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class AccountInfo {
    @Min(6)
    @NotBlank
    private String accountNumber;
    @Min(6)
    @NotBlank
    private Long bic;
    private Double balance;
}
