package com.bank_fx.transaction.domain.repository;

import com.bank_fx.transaction.domain.model.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountNumber(String accountNumber);

    User save(User user);

    void deleteById(Long id);
}
