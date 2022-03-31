package ru.jtc.moneytrans.rest.dto;

import lombok.Data;

@Data
public class BaseResponse {

    private final String STATUS;
    private final String MESSAGE;

}
