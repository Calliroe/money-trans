package ru.jtc.moneytrans.rest.validation.response;

import lombok.Data;
import ru.jtc.moneytrans.rest.validation.violation.ApplicationViolation;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorResponse {

    private List<ApplicationViolation> violations = new ArrayList<>();

}
