package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

@Data
public class TransferMoneyDto {
    private long payerAccountNumber;
    private long receiverAccountNumber;
    private double amount;
}
