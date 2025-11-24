package com.bank_fx.transaction.infrastructure.persistance.jpa;

import com.bank_fx.transaction.domain.model.BankAccount;
import com.bank_fx.transaction.domain.repository.BankAccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaBankAccountRepository
        extends JpaRepository<BankAccount, Long>, BankAccountRepository {

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
