package com.example.bankdemo.controller;

import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bankdemo.service.CardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final CardService cardService;

    /*
        ADMIN:
        View all cards with pagination
    */
    @GetMapping("/cards")
    public ResponseEntity<Page<Card>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cardService.getAll(PageRequest.of(page, size)));
    }

    /*
        ADMIN:
        Filter cards by status

        Example:
        /admin/cards/status/ACTIVE
    */
    @GetMapping("/cards/status/{status}")
    public ResponseEntity<Page<Card>> getByStatus(
            @PathVariable CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cardService.getByStatus(status, PageRequest.of(page, size)));
    }

    /*
        ADMIN:
        Activate card
    */
    @PutMapping("/cards/{id}/activate")
    public ResponseEntity<Card> activate(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.activate(id));
    }

    /*
        ADMIN:
        Block card
    */
    @PutMapping("/cards/{id}/block")
    public ResponseEntity<Card> block(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.block(id));
    }

    /*
        ADMIN:
        Delete card
    */
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.ok("Card deleted");
    }
}