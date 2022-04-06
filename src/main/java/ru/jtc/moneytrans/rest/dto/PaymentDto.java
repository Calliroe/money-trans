package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class PaymentDto {

    @Size(min = 6, max = 6, message = "Номер счёта должен содержать 6 символов")
    @NotBlank(message = "Введите счёт отправителя")
    private String payerAccountNumber;
    @Size(min = 6, max = 6, message = "Номер счёта должен содержать 6 символов")
    @NotBlank(message = "Введите счёт получателя")
    private String receiverAccountNumber;
    @Min(value = 1, message = "Минимальная сумма перевода - 1 рубль")
    @NotNull(message = "Введите сумму перевода")
    private BigDecimal amount;

}
