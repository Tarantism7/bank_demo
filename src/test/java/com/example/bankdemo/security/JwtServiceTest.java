package com.example.bankdemo.security;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private final JwtService jwtService =
            new JwtService();

    @Test
    void shouldGenerateToken(){
        String username =
                "testuser";
        String token =
                jwtService.generateToken(username);
        assertNotNull(token);
        assertEquals(
                username,
                jwtService.extractUsername(token)
        );
    }

    @Test
    void tokenShouldBeValid(){
        String token =
                jwtService.generateToken(
                        "admin"
                );
        assertTrue(
                jwtService.isValid(token)
        );
    }
}