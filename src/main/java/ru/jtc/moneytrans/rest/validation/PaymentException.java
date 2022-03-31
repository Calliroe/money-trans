package ru.jtc.moneytrans.rest.validation;

public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }

}
