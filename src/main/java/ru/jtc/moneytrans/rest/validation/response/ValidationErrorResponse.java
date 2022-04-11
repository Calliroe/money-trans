package ru.jtc.moneytrans.rest.validation.response;

import lombok.Data;
import ru.jtc.moneytrans.rest.validation.violation.ValidationViolation;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {

    private List<ValidationViolation> violations = new ArrayList<>();

}
