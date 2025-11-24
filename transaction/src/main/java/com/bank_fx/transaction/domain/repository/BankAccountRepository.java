package com.bank_fx.transaction.domain.repository;

import com.bank_fx.transaction.domain.model.BankAccount;
import java.util.Optional;

public interface BankAccountRepository {

    Optional<BankAccount> findById(Long id);
    Optional<BankAccount> findByAccountNumber(String accountNumber);

    BankAccount save(BankAccount account);

    void deleteById(Long id);
}
