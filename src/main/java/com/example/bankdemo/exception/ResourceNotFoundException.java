package com.example.bankdemo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resourceName, String identifier) {
        super(
                resourceName.toUpperCase() + "_NOT_FOUND",
                String.format("%s with identifier '%s' was not found.", resourceName, identifier),
                HttpStatus.NOT_FOUND
        );
    }
}