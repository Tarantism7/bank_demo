package com.example.bankdemo.repository;

import com.example.bankdemo.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // Custom query to fetch transfers where the user is either sender or receiver
    @Query("SELECT t FROM Transfer t WHERE t.fromCard.user.id = :userId OR t.toCard.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transfer> findTop10ByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}