package com.bank_fx.transaction.infrastructure.persistance.jpa;


import com.bank_fx.transaction.domain.model.Transaction;
import com.bank_fx.transaction.domain.repository.TransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaTransactionRepository
        extends JpaRepository<Transaction, Long>, TransactionRepository {

    List<Transaction> findByFromAccountOrToAccount(String fromAccount, String toAccount);

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount = :accountNumber OR t.toAccount = :accountNumber) AND t.timestamp BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountAndDateRange(@Param("accountNumber") String accountNumber,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}
