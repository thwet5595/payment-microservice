package org.wavemoney.payment.api.exception.validation;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
