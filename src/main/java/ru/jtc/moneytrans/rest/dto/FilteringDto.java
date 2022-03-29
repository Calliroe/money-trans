package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FilteringDto {
    private Long payerAccountId;
    private Long receiverAccountId;
    private Double amount;
    private Date createDate;
}
