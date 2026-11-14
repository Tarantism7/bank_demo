package com.example.bankdemo.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends AppException {
    protected InsufficientBalanceException(String accountNumber, Double currentBalance, Double requestedAmount) {
        super("INSUFFICIENT_FUNDS",
                String.format("Account %s has insufficient funds. Current balance: %.2f, Requested: %.2f",
                        accountNumber, currentBalance, requestedAmount),
                HttpStatus.BAD_REQUEST);
    }
}
