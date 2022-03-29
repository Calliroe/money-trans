package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class PaymentDto {
    @Min(6)
    @NotBlank
    private String payerAccountNumber;
    @Min(6)
    @NotBlank
    private String receiverAccountNumber;
    @Min(1)
    @NotBlank
    private Double amount;
}
