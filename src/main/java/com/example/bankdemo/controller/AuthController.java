package com.example.bankdemo.controller;

import com.example.bankdemo.dto.LoginRequest;
import com.example.bankdemo.dto.RegisterRequest;
import com.example.bankdemo.enums.Role;
import jakarta.validation.Valid;
import com.example.bankdemo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankdemo.security.JwtService;
import com.example.bankdemo.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.exists(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.USER);

        userService.save(user);

        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        if (userService.exists(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.ADMIN);

        userService.save(user);

        return ResponseEntity.ok("Admin registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        String token = jwtService.generateToken(request.getUsername());

        Map<String, String> response = new HashMap<>();

        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
