package com.example.tracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputParametersException extends RuntimeException {

    public InvalidInputParametersException(String message) {
        super(message);
    }

    public InvalidInputParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
