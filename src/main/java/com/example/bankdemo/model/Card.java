package com.example.bankdemo.model;

import com.example.bankdemo.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.bankdemo.enums.CardType;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String number;

    @Column(unique = true, nullable = false)
    private String numberHash; // used for duplicate checking

    private String owner;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "card_name")
    private String cardName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;
}
