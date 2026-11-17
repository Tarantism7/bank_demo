package com.example.bankdemo.controller;

import com.example.bankdemo.enums.Role;
import com.example.bankdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Controller
@RequiredArgsConstructor
public class WebAuthController {

    private final UserService userService;

    // Change this (application.yml: app.admin-invite-code, or env APP_ADMIN_INVITE_CODE).
    // Leave it empty to switch admin self-registration off completely.
    @Value("${app.admin-invite-code:demo-admin-code}")
    private String adminInviteCode;

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            return "redirect:" + (admin ? "/admin/dashboard" : "/user/dashboard");
        }
        return "home";
    }

   //old links
    @GetMapping("/login")
    public String login() {
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@RequestParam("accountType") String accountType,
                           @RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam(value = "inviteCode", required = false) String inviteCode,
                           Model model) {
        boolean admin = "admin".equalsIgnoreCase(accountType);
        try {
            if (admin && !inviteCodeMatches(inviteCode)) {
                throw new IllegalArgumentException("Invalid admin invite code.");
            }
            userService.register(username, email, password, admin ? Role.ADMIN : Role.USER);
            return "redirect:/?registered&type=" + (admin ? "admin" : "user");
        } catch (IllegalArgumentException e) {
            model.addAttribute("registerError", e.getMessage());
            model.addAttribute("registerType", admin ? "admin" : "user");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "home";
        }
    }

    private boolean inviteCodeMatches(String provided) {
        if (adminInviteCode == null || adminInviteCode.isBlank() || provided == null) {
            return false;
        }
        return MessageDigest.isEqual(
                adminInviteCode.getBytes(StandardCharsets.UTF_8),
                provided.getBytes(StandardCharsets.UTF_8));
    }
}
