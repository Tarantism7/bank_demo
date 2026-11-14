package com.example.bankdemo.controller;

import com.example.bankdemo.security.JwtFilter;
import com.example.bankdemo.security.JwtService;
import com.example.bankdemo.security.SecurityConfig;
import com.example.bankdemo.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@EnableWebSecurity // <--- Annotate here instead of putting it in @Import
@Import({
        SecurityConfig.class,
        JwtFilter.class,
        AuthenticationConfiguration.class
})
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Should return 401 Unauthorized when no JWT token is provided")
    void shouldReturnUnauthorizedWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when invalid JWT token is provided")
    void shouldReturnUnauthorizedWithInvalidJwt() throws Exception {
        String invalidToken = "invalid-token";

        when(jwtService.isValid(invalidToken)).thenReturn(false);

        mockMvc.perform(get("/api/cards")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 200 OK when valid JWT token is provided")
    void shouldReturnOkWithValidJwt() throws Exception {
        String validToken = "valid.jwt.token";
        String username = "testuser";

        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .roles("USER")
                .build();

        when(jwtService.isValid(validToken)).thenReturn(true);
        when(jwtService.extractUsername(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        mockMvc.perform(get("/api/cards")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }
}