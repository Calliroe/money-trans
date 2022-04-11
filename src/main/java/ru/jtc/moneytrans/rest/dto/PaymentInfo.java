package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class PaymentInfo {

    @Size(min = 6, max = 6, message = "Номер счёта должен содержать 6 символов")
    @NotBlank(message = "Введите счёт отправителя")
    private String payerAccountNumber;
    @NotNull(message = "Введите БИК отправителя")
    private Long payerBic;
    @Size(min = 6, max = 6, message = "Номер счёта должен содержать 6 символов")
    @NotBlank(message = "Введите счёт получателя")
    private String receiverAccountNumber;
    @NotNull(message = "Введите БИК получателя")
    private Long receiverBic;
    @Min(value = 1, message = "Минимальная сумма перевода - 1 рубль")
    @NotNull(message = "Введите сумму перевода")
    private BigDecimal amount;
    private String comment;

}
