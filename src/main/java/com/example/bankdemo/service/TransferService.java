
package com.example.bankdemo.service;

import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import com.example.bankdemo.model.Card;
import com.example.bankdemo.model.Transfer;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankdemo.repository.CardRepository;
import com.example.bankdemo.repository.TransferRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    @Transactional
    public Transfer transfer(Long fromId, Long toId, BigDecimal amount) {

        Card from = cardRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Source card not found"));

        Card to = cardRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Target card not found"));

        if (from.getId().equals(to.getId())) {
            throw new RuntimeException("Cannot transfer to the same card");
        }

        if (from.getStatus() != CardStatus.ACTIVE ||
                to.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Both cards must be active");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
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
