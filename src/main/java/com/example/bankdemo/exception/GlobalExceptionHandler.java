package com.example.bankdemo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles @Valid / DTO validation errors (e.g. invalid card numbers, negative transfer amounts)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for request: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("VALIDATION_ERROR");
        problem.setDetail("One or more request fields failed validation.");
        problem.setType(URI.create("https://bankdemo.com/errors/validation-error"));

        // Structure field errors as a key-value Map instead of concatenated String
        Map<String, String> invalidFields = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            invalidFields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        problem.setProperty("invalidFields", invalidFields);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.of(problem).build();
    }

    /**
     * Handles domain business exceptions (e.g., InsufficientBalance, CardBlocked, ResourceNotFound)
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ProblemDetail> handleAppException(AppException e) {
        log.warn("Business Exception [{}] : {}", e.getErrorCode(), e.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(e.getHttpStatus());
        problem.setTitle(e.getErrorCode());
        problem.setDetail(e.getMessage());
        problem.setType(URI.create("https://bankdemo.com/errors/" + e.getErrorCode().toLowerCase()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.of(problem).build();
    }

    /**
     * Handles unexpected system failures (Database down, NullPointerExceptions)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnknown(Exception e) {
        // Generate unique tracking code for production logs
        String errorReference = UUID.randomUUID().toString();
        log.error("Unhandled error occurred [Ref ID: {}]", errorReference, e);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("INTERNAL_SERVER_ERROR");
        problem.setDetail("An unexpected error occurred. Please contact support with reference ID: " + errorReference);
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errorReference", errorReference);

        return ResponseEntity.of(problem).build();
    }
}