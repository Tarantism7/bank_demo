package com.example.bankdemo.repository;
import com.example.bankdemo.enums.CardStatus;
import com.example.bankdemo.model.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByUserId(Long userId, Pageable pageable);
    Page<Card> findByStatus(CardStatus status, Pageable pageable);
    boolean existsByNumberHash(String numberHash);
    // Finds all cards belonging to a specific user ID
    List<Card> findByUserId(Long userId);

    //count active cards for admin
    long countByStatus(com.example.bankdemo.enums.CardStatus status);

    @Query("SELECT COALESCE(SUM(c.balance), 0) FROM Card c")
    BigDecimal sumBalances();

    List<Card> findByUserIdAndStatusNot(
            Long userId,
            CardStatus status
    );

    Optional<Card> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.id = :id")
    Optional<Card> findByIdForUpdate(@Param("id") Long id);
}
