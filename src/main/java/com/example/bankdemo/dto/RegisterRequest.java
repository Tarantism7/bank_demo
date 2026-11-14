package com.example.bankdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must contain at least 6 characters")
    private String password;

    @NotBlank
    @jakarta.validation.constraints.Email
    private String email;

    public RegisterRequest() {
    }
}
