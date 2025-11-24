package com.bank_fx.transaction.application.service;

import com.bank_fx.transaction.domain.model.*;
import com.bank_fx.transaction.application.dto.UserRegistrationDto;
import com.bank_fx.transaction.application.exception.UserAlreadyExistsException;
import com.bank_fx.transaction.application.exception.UserNotFoundException;
import com.bank_fx.transaction.domain.repository.UserRepository;
import com.bank_fx.transaction.domain.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    public UserService(UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + registrationDto.getEmail() + " already exists");
        }

        // Create user
        User user = new User(
                registrationDto.getEmail(),
                registrationDto.getPassword(), // In real app, encrypt password
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                UserRole.CUSTOMER
        );

        User savedUser = userRepository.save(user);

        // Create bank account
        String accountNumber = generateAccountNumber();
        BankAccount account = new BankAccount(
                accountNumber,
                registrationDto.getCurrency(),
                savedUser
        );

        if (registrationDto.getInitialDeposit() != null &&
                registrationDto.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            account.deposit(registrationDto.getInitialDeposit());
        }

        bankAccountRepository.save(account);
        savedUser.setBankAccount(account);

        return userRepository.save(savedUser);
    }

    public User blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setBlocked(true);
        return userRepository.save(user);
    }

    public User unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setBlocked(false);
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
}