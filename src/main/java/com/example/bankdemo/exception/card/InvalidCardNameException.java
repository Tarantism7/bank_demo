package com.example.bankdemo.exception.card;

import com.example.bankdemo.exception.AppException;
import org.springframework.http.HttpStatus;

public class InvalidCardNameException extends AppException {

    public InvalidCardNameException(String message) {
        super(
                "INVALID_CARD_NAME",
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}