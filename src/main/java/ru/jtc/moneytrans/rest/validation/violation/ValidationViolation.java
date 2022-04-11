package ru.jtc.moneytrans.rest.validation.violation;

import lombok.Data;

@Data
public class ValidationViolation {

    private final String fieldName;
    private final String message;

}
