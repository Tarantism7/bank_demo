package com.example.bankdemo.service;

import com.example.bankdemo.enums.Role;
import lombok.RequiredArgsConstructor;
import com.example.bankdemo.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.bankdemo.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));
    }

    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Creates an account (used by the main page).
     * Throws IllegalArgumentException with a message that can be shown to the visitor.
     */
    public User register(String username, String email, String rawPassword, Role role) {
        String name = username == null ? "" : username.trim();
        String mail = email == null ? "" : email.trim().toLowerCase();

        if (name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Username must be 3 to 50 characters.");
        }
        if (!mail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || mail.length() > 255) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (userRepository.existsByUsername(name)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.existsByEmail(mail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = User.builder()
                .username(name)
                .email(mail)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public long countAllUsers() {
        return userRepository.count();
    }
}