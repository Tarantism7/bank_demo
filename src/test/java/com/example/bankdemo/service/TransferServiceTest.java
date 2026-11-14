package com.example.bankdemo.service;

import com.example.bankdemo.model.Card;
import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.repository.CardRepository;
import com.example.bankdemo.repository.TransferRepository;
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
class TransferServiceTest {

    @Mock
    CardRepository cardRepository;

    @Mock
    TransferRepository transferRepository;

    @InjectMocks
    TransferService transferService;

    @Test
    void shouldTransferMoney(){
        Card from = new Card();
        from.setId(1L);
        from.setBalance(
                new BigDecimal("1000")
        );
        from.setStatus(
                CardStatus.ACTIVE
        );
        Card to = new Card();
        to.setId(2L);
        to.setBalance(
                new BigDecimal("200")
        );
        to.setStatus(
                CardStatus.ACTIVE
        );

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(from));
        when(cardRepository.findById(2L))
                .thenReturn(Optional.of(to));

        transferService.transfer(
                1L,
                2L,
                new BigDecimal("300")
        );
        assertEquals(
                new BigDecimal("700"),
                from.getBalance()
        );
        assertEquals(
                new BigDecimal("500"),
                to.getBalance()
        );
        verify(cardRepository,times(2))
                .save(any(Card.class));
    }
}