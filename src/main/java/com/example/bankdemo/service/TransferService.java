package com.example.bankdemo.service;

import com.example.bankdemo.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import com.example.bankdemo.model.Card;
import com.example.bankdemo.model.Transfer;
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

        if (from.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Source card is not active");
        }
        if (to.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Target card is not active");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);

        Transfer transfer = new Transfer();

        transfer.setFromCard(from);
        transfer.setToCard(to);
        transfer.setAmount(amount);
        transfer.setCreatedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    public List<Transfer> getRecentTransactionsForUser(Long userId) {
        return transferRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }
}
