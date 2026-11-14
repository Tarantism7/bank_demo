package com.example.bankdemo.service;

import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.model.Card;
import com.example.bankdemo.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void shouldBlockCard() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        cardService.block(1L);

        assertEquals(
                CardStatus.BLOCKED,
                card.getStatus()
        );
        verify(cardRepository)
                .save(card);
    }

    @Test
    void shouldReturnBalance() {
        Card card = new Card();
        card.setId(1L);
        card.setBalance(
                new BigDecimal("1000")
        );

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        BigDecimal result =
                cardService.getBalance(1L);

        assertEquals(
                new BigDecimal("1000"),
                result
        );
    }
}