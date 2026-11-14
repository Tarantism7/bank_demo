package com.example.bankdemo.exception;

import org.springframework.http.HttpStatus;

public class CardBlockedException extends AppException {
    public CardBlockedException(String cardNumber) {
        super(
                "CARD_BLOCKED",
                String.format("Card ending in %s is blocked or inactive.",
                        cardNumber.substring(cardNumber.length() - 4)),
                HttpStatus.FORBIDDEN
        );
    }
}