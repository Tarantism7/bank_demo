package com.example.bankdemo.exception;

import org.springframework.http.HttpStatus;

public class CardAlreadyExistsException extends AppException {

    public CardAlreadyExistsException(String cardNumber) {
        super(
                "CARD_ALREADY_EXISTS",
                String.format("Card ending in %s already exists.",
                        cardNumber.substring(cardNumber.length() - 4)),
                HttpStatus.CONFLICT
        );
    }
}
