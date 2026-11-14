package com.example.bankdemo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // Use standard BCryptPasswordEncoder to match your Liquibase seeded passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // Creating admins through the REST API now needs an admin token.
                        // (Browser admin sign-up goes through the main page and its invite code.)
                        .requestMatchers("/auth/register-admin").hasRole("ADMIN")
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/error",
                                "/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/")                 // the main page is the login page
                        .loginProcessingUrl("/login")   // where the main page posts the sign-in form
                        .successHandler(this::onLoginSuccess)
                        .failureHandler(this::onLoginFailure)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/?logout"))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * The main page lets the visitor choose "User" or "Admin" sign-in (form field loginType).
     * The choice has to match the role of the account, otherwise the session is dropped again.
     */
    private void onLoginSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        String type = loginType(request);

        if (isAdmin != "admin".equals(type)) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            response.sendRedirect(request.getContextPath() + "/?roleError&type=" + type);
            return;
        }

        response.sendRedirect(request.getContextPath() + (isAdmin ? "/admin/dashboard" : "/user/dashboard"));
    }

    private void onLoginFailure(HttpServletRequest request,
                                HttpServletResponse response,
                                AuthenticationException exception) throws IOException {
        response.sendRedirect(request.getContextPath() + "/?error&type=" + loginType(request));
    }

    private String loginType(HttpServletRequest request) {
        return "admin".equalsIgnoreCase(request.getParameter("loginType")) ? "admin" : "user";
    }
}