package com.example.bankdemo.dto;

import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class CardResponse {
    private Long id;

    private String number;

    private String owner;

    private LocalDate expiryDate;

    private String status;

    private BigDecimal balance;

    public CardResponse() {
    }
}