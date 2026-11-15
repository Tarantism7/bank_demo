package com.example.bankdemo.exception.card;

import com.example.bankdemo.exception.AppException;
import org.springframework.http.HttpStatus;

public class UnauthorizedCardAccessException extends AppException {

    public UnauthorizedCardAccessException() {
        super(
                "UNAUTHORIZED_CARD_ACCESS",
                "You cannot modify another user's card.",
                HttpStatus.FORBIDDEN
        );
    }
}
