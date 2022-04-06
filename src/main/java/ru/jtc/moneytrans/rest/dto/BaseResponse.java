package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

@Data
public class BaseResponse {

    private final String status;
    private final String message;

}
