package com.example.tracking.exception;

public class InvalidCountryCodeException extends InvalidInputParametersException {

    public InvalidCountryCodeException(String message) {
        super(message);
    }

    public InvalidCountryCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
