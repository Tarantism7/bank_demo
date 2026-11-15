package com.example.bankdemo.exception.transaction;

import com.example.bankdemo.exception.AppException;
import org.springframework.http.HttpStatus;

public class InvalidAmountException extends AppException {

    public InvalidAmountException() {
        super(
                "INVALID_AMOUNT",
                "Amount must be greater than zero.",
                HttpStatus.BAD_REQUEST
        );
    }
}
