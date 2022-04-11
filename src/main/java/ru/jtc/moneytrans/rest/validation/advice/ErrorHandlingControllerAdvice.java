package ru.jtc.moneytrans.rest.validation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.jtc.moneytrans.rest.validation.exception.ApplicationException;
import ru.jtc.moneytrans.rest.validation.response.ErrorResponse;
import ru.jtc.moneytrans.rest.validation.response.ValidationErrorResponse;
import ru.jtc.moneytrans.rest.validation.violation.ApplicationViolation;
import ru.jtc.moneytrans.rest.validation.violation.ValidationViolation;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new ValidationViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorResponse processException(ApplicationException e) {
        ErrorResponse error = new ErrorResponse();
        error.getViolations().add(new ApplicationViolation(e.getMessage()));
        return error;
    }

}
