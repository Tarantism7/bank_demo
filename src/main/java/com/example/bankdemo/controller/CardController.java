package com.example.bankdemo.controller;

import com.example.bankdemo.dto.CardResponse;
import jakarta.validation.Valid;
import com.example.bankdemo.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.bankdemo.service.CardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    /*
        USER:
        View own cards with pagination
    */
    @GetMapping("/my")
    public ResponseEntity<List<CardResponse>> myCards(Authentication authentication) {
        // In a real implementation:
        // username -> User -> userId
        Long userId = 1L;
        return ResponseEntity.ok(cardService.getUserCards(userId));
    }

    /*
        USER:
        Create card
        Usually ADMIN only,
        kept here for simplicity
    */
    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody Card card, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cardService.create(card, username));
    }

    /*
        USER:
        Request card blocking
    */
    @PutMapping("/{id}/block")
    public ResponseEntity<Card> block(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.block(id));
    }

    /*
        USER:
        Check balance
    */
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> balance(@PathVariable Long id) {
        Card card = cardService.getCard(id);
        return ResponseEntity.ok(card.getBalance());
    }

    /*
        ADMIN:
        Pagination example
    */
    @GetMapping("/all")
    public ResponseEntity<Page<Card>> allCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cardService.getAll(PageRequest.of(page, size)));
    }
}