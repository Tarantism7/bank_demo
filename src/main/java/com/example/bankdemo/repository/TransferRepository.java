
package com.example.bankdemo.repository;

import org.springframework.data.domain.Pageable;
import com.example.bankdemo.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("""
        SELECT t
        FROM Transfer t
        LEFT JOIN t.fromCard fc
        LEFT JOIN t.toCard tc
        WHERE fc.user.id = :userId
           OR tc.user.id = :userId
        ORDER BY t.createdAt DESC
    """)
    List<Transfer> findRecentTransactions(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
