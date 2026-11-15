package com.example.bankdemo.exception.card;

import com.example.bankdemo.exception.AppException;
import org.springframework.http.HttpStatus;

public class CardNotActiveException extends AppException {

    public CardNotActiveException() {
        super(
                "CARD_NOT_ACTIVE",
                "Cannot add balance to an inactive card.",
                HttpStatus.CONFLICT
        );
    }
}
