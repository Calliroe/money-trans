package ru.jtc.moneytrans.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentFilter {

    private String payerAccountNumber;
    private String receiverAccountNumber;
    private Long payerBic;
    private Long receiverBic;
    private BigDecimal amount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate createDate;

}
