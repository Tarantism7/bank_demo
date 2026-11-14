package com.example.bankdemo.util;

public class CardMaskUtil {

    public static String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {return "****";}
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}