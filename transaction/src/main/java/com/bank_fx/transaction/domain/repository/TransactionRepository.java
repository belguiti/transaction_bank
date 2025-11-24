package com.bank_fx.transaction.domain.repository;

import com.bank_fx.transaction.domain.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> findById(Long id);

    List<Transaction> findByFromAccountOrToAccount(String fromAccount, String toAccount);

    List<Transaction> findByAccountAndDateRange(String accountNumber,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate);

    Transaction save(Transaction transaction);

    void deleteById(Long id);
}
