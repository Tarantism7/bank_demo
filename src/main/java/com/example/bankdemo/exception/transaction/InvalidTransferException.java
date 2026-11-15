package com.example.bankdemo.exception.transaction;

import com.example.bankdemo.exception.AppException;
import org.springframework.http.HttpStatus;

public class InvalidTransferException extends AppException {
    public InvalidTransferException(String message) {
        super("INVALID_TRANSFER_OPERATION", message, HttpStatus.BAD_REQUEST);
    }
}