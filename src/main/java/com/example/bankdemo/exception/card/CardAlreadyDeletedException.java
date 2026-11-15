package com.example.bankdemo.exception.card;

import com.example.bankdemo.exception.AppException;
import org.springframework.http.HttpStatus;

public class CardAlreadyDeletedException extends AppException {

    public CardAlreadyDeletedException(Long cardId) {
        super(
                "CARD_ALREADY_DELETED",
                "Card with ID " + cardId + " is already deleted.",
                HttpStatus.CONFLICT
        );
    }
}