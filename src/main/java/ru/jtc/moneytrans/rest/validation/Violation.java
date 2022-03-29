package ru.jtc.moneytrans.rest.validation;

import lombok.Data;

@Data
public class Violation {
    private final String fieldName;
    private final String message;
}
