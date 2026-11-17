
package com.example.bankdemo.service;

import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.enums.TransactionType;
import com.example.bankdemo.model.Card;
import com.example.bankdemo.model.Transfer;
import com.example.bankdemo.model.User;
import com.example.bankdemo.repository.CardRepository;
import com.example.bankdemo.repository.TransferRepository;
import com.example.bankdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/*
TODO: update exception
 */

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    //transfer between own cards
    @Transactional
    public Transfer transfer(
            Long fromId,
            Long toId,
            BigDecimal amount,
            String senderUsername
    ) {
        validateAmount(amount);

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same card."
            );
        }

        Card from = cardRepository.findByIdForUpdate(fromId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Source card not found."));

        Card to = cardRepository.findByIdForUpdate(toId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Target card not found."));

        if (from.getUser() == null ||
                !from.getUser().getUsername().equals(senderUsername)) {
            throw new IllegalArgumentException(
                    "You do not own the source card."
            );
        }

        if (to.getUser() == null ||
                !to.getUser().getId().equals(from.getUser().getId())) {
            throw new IllegalArgumentException(
                    "You can only transfer between your own cards."
            );
        }

        return executeTransfer(from, to, amount);
    }

    //transfer money to another registered user
    @Transactional
    public Transfer transferToUser(
            String senderUsername,
            String recipientUsername,
            Long fromCardId,
            Long toCardId,
            BigDecimal amount
    ) {

        validateAmount(amount);

        if (fromCardId.equals(toCardId)) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same card."
            );
        }

        String recipientName = recipientUsername == null
                ? ""
                : recipientUsername.trim();

        if (recipientName.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient username is required."
            );
        }

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sender not found."));

        User recipient = userRepository.findByUsername(recipientName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recipient username does not exist."
                        ));

        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException(
                    "You cannot send money to your own account."
            );
        }

        // Lock cards in ascending ID order to reduce deadlock risk.
        Long firstId = Math.min(fromCardId, toCardId);
        Long secondId = Math.max(fromCardId, toCardId);

        Card first = cardRepository.findByIdForUpdate(firstId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Card not found."));

        Card second = cardRepository.findByIdForUpdate(secondId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Card not found."));

        Card from = fromCardId.equals(firstId) ? first : second;
        Card to = toCardId.equals(firstId) ? first : second;

        // Verify ownership on the server.
        if (from.getUser() == null ||
                !from.getUser().getId().equals(sender.getId())) {
            throw new IllegalArgumentException(
                    "You do not own the selected source card."
            );
        }

        if (to.getUser() == null ||
                !to.getUser().getId().equals(recipient.getId())) {
            throw new IllegalArgumentException(
                    "The destination card does not belong to this recipient."
            );
        }

        return executeTransfer(from, to, amount);
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        if (amount.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(
                    "Amount cannot have more than two decimal places."
            );
        }
    }

    private Transfer executeTransfer(
            Card from,
            Card to,
            BigDecimal amount
    ) {

        if (from.getStatus() != CardStatus.ACTIVE ||
                to.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Both cards must be active."
            );
        }

        if (from.getBalance() == null ||
                to.getBalance() == null ||
                from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);

        Transfer transfer = Transfer.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .type(TransactionType.TRANSFER)
                .build();

        return transferRepository.save(transfer);
    }

    public List<Transfer> getRecentTransactionsForUser(Long userId) {
        return transferRepository.findRecentTransactions(
                userId,
                PageRequest.of(0, 10)
        );
    }
}
