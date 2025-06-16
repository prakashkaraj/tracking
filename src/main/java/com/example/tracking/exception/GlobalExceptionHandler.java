package com.example.tracking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        String requestUri = request.getDescription(false).substring(4);
        logger.warn("ConstraintViolationException for request URI [{}]: {}", requestUri, ex.getMessage());

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String formattedError = formatViolationMessage(violation);
                    logger.debug("Validation error detail: {}", formattedError);
                    return formattedError;
                })
                .collect(Collectors.toList());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", "Validation failed");
        body.put("errors", errors);
        body.put("path", requestUri);

        logger.info("Responding with BAD_REQUEST for ConstraintViolationException. Path: {}, Errors: {}", requestUri, errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private String formatViolationMessage(ConstraintViolation<?> violation) {
        String parameterName = violation.getPropertyPath().toString();
        if (parameterName.contains(".")) {
            parameterName = parameterName.substring(parameterName.lastIndexOf('.') + 1);
        }
        return parameterName + ": " + violation.getMessage();
    }

    @ExceptionHandler(InvalidInputParametersException.class)
    public ResponseEntity<Object> handleInvalidInputParametersException(InvalidInputParametersException ex, WebRequest request) {
        String requestUri = request.getDescription(false).substring(4);
        logger.warn("InvalidInputParametersException for request URI [{}]: {}", requestUri, ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", requestUri);

        logger.info("Responding with BAD_REQUEST for InvalidInputParametersException. Path: {}, Message: {}", requestUri, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String requestUri = request.getDescription(false).substring(4);
        String parameterName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        Object value = ex.getValue();

        String message = String.format("Invalid value for parameter '%s'. Expected type '%s', but received value '%s'.",
                parameterName, requiredType, value);

        logger.warn("MethodArgumentTypeMismatchException for request URI [{}]: {}", requestUri, message);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", message);
        body.put("path", requestUri);

        logger.info("Responding with BAD_REQUEST for MethodArgumentTypeMismatchException. Path: {}, Message: {}", requestUri, message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String requestUri = request.getDescription(false).substring(4);
        logger.error("RuntimeException for request URI [{}]: {}", requestUri, ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", "An unexpected error occurred. Please try again later.");
        body.put("path", requestUri);
        
        logger.info("Responding with INTERNAL_SERVER_ERROR for RuntimeException. Path: {}", requestUri);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
