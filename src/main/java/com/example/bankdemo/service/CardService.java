package com.example.bankdemo.service;

import com.example.bankdemo.dto.CardResponse;
import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.exception.CardAlreadyExistsException;
import com.example.bankdemo.model.User;
import com.example.bankdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.bankdemo.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.bankdemo.repository.CardRepository;
import com.example.bankdemo.util.CardMaskUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final HashingService hashingService;

    public CardResponse create(Card card, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String originalNumber = card.getNumber();
        String hash = hashingService.hash(originalNumber);

        if (cardRepository.existsByNumberHash(hash)) {
            throw new CardAlreadyExistsException(originalNumber);
        }

        card.setUser(user);
        card.setNumberHash(hash);
        card.setNumber(encryptionService.encrypt(originalNumber));
        card.setStatus(CardStatus.ACTIVE);

        Card saved = cardRepository.save(card);

        return toResponse(saved);
    }

    public Card block(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }

    public Card activate(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setStatus(CardStatus.ACTIVE);
        return cardRepository.save(card);
    }

    public void delete(Long id) {
        cardRepository.deleteById(id);
    }

    public Page<Card> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    public Page<Card> getByStatus(CardStatus status, Pageable pageable) {
        return cardRepository.findByStatus(status, pageable);
    }

    public List<CardResponse> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId, Pageable.unpaged())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CardResponse toResponse(Card card) {
        CardResponse dto = new CardResponse();
        dto.setId(card.getId());
        dto.setOwner(card.getOwner());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus().name());
        dto.setBalance(card.getBalance());
        String decrypted = encryptionService.decrypt(card.getNumber());
        dto.setNumber(CardMaskUtil.mask(decrypted));
        return dto;
    }

    public Card getCard(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    public BigDecimal getBalance(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return card.getBalance();
    }

    /**
     * Fetch all cards for a specific user ID.
     */
    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    /**
     * Helper for admin dashboard if needed.
     */
    public long countActiveCards() {
        return cardRepository.countByStatus(CardStatus.ACTIVE);
    }

    public BigDecimal totalBalance() {
        return cardRepository.sumBalances();
    }
}
