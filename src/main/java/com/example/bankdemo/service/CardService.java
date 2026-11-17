package com.example.bankdemo.service;

import com.example.bankdemo.dto.CardResponse;
import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.enums.CardType;
import com.example.bankdemo.enums.TransactionType;
import com.example.bankdemo.exception.ResourceNotFoundException;
import com.example.bankdemo.exception.card.*;
import com.example.bankdemo.exception.transaction.InvalidAmountException;
import com.example.bankdemo.model.Transfer;
import com.example.bankdemo.model.User;
import com.example.bankdemo.repository.TransferRepository;
import com.example.bankdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.bankdemo.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.bankdemo.repository.CardRepository;
import com.example.bankdemo.util.CardMaskUtil;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final HashingService hashingService;

    private static final SecureRandom RANDOM = new SecureRandom();
    private final TransferRepository transferRepository;

    public CardResponse create(Card card, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", username)
                );

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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", id.toString())
                );
        card.setStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }

    public Card activate(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", id.toString())
                );
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", id.toString())
                );
    }

    public BigDecimal getBalance(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", id.toString())
                );
        return card.getBalance();
    }

    public long countActiveCards() {
        return cardRepository.countByStatus(CardStatus.ACTIVE);
    }

    public BigDecimal totalBalance() {
        return cardRepository.sumBalances();
    }

    public CardResponse createForUser(
            String username,
            String cardName,
            CardType cardType) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", username)
                );

        Card card = new Card();

        card.setNumber(generateCardNumber());
        card.setOwner(user.getUsername());
        card.setCardName(cardName);
        card.setCardType(cardType);
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setBalance(BigDecimal.ZERO);

        return create(card, username);
    }

    private String generateCardNumber() {

        StringBuilder number = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            number.append(RANDOM.nextInt(10));
        }

        return number.toString();
    }

    public Card renameCard(Long cardId, String newName, String username) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", cardId.toString())
                );

        if (!card.getUser().getUsername().equals(username)) {
            throw new UnauthorizedCardAccessException();
        }

        if (newName == null || newName.isBlank()) {
            throw new InvalidCardNameException("Card name cannot be empty");
        }

        String trimmedName = newName.trim();

        if (trimmedName.length() > 50) {
            throw new InvalidCardNameException("Card name is too long");
        }

        card.setCardName(trimmedName);

        return cardRepository.save(card);
    }

    public void deleteUserCard(Long cardId, Long userId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", cardId.toString())
                );

        // Check card ownership
        if (!card.getUser().getId().equals(userId)) {
            throw new UnauthorizedCardAccessException();
        }

        // Check if already deleted
        if (card.getStatus() == CardStatus.DELETED) {
            throw new CardAlreadyDeletedException(cardId);
        }

        // Soft delete
        card.setStatus(CardStatus.DELETED);
        cardRepository.save(card);
    }

    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.findByUserIdAndStatusNot(
                userId,
                CardStatus.DELETED
        );
    }

    @Transactional
    public void addBalance(Long cardId, Long userId, BigDecimal amount) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card", cardId.toString())
                );

        if (!card.getUser().getId().equals(userId)) {
            throw new UnauthorizedCardAccessException();
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException();
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        Transfer deposit = Transfer.builder()
                .fromCard(null)
                .toCard(card)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .type(TransactionType.DEPOSIT)
                .build();

        transferRepository.save(deposit);
    }
}
