package com.bank_fx.transaction.infrastructure.persistance.jpa;

import com.bank_fx.transaction.domain.model.User;
import com.bank_fx.transaction.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository
        extends JpaRepository<User, Long>, UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountNumber(String accountNumber);
}
