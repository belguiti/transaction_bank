package com.bank_fx.transaction.application.service;


import com.bank_fx.transaction.domain.model.*;
import com.bank_fx.transaction.application.annotation.TransactionLog;
import com.bank_fx.transaction.application.dto.TransactionRequest;
import com.bank_fx.transaction.application.exception.InsufficientBalanceException;
import com.bank_fx.transaction.application.exception.UserBlockedException;
import com.bank_fx.transaction.domain.repository.UserRepository;
import com.bank_fx.transaction.domain.repository.BankAccountRepository;
import com.bank_fx.transaction.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class TransactionService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final ForexService forexService;

    public TransactionService(UserRepository userRepository,
                              BankAccountRepository bankAccountRepository,
                              TransactionRepository transactionRepository,
                              ForexService forexService) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.forexService = forexService;
    }

    @TransactionLog
    public Transaction transfer(TransactionRequest request) {
        // Validate users are not blocked
        User fromUser = userRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        User toUser = userRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromUser.isBlocked() || toUser.isBlocked()) {
            throw new UserBlockedException("One or both users are blocked");
        }

        BankAccount fromAccount = fromUser.getBankAccount();
        BankAccount toAccount = toUser.getBankAccount();

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        Transaction transaction;

        if (fromAccount.getCurrency() == toAccount.getCurrency()) {
            // Local transaction
            transaction = performLocalTransfer(fromAccount, toAccount, request.getAmount());
        } else {
            // Forex transaction
            transaction = performForexTransfer(fromAccount, toAccount, request.getAmount());
        }

        return transactionRepository.save(transaction);
    }

    private Transaction performLocalTransfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                amount,
                fromAccount.getCurrency(),
                toAccount.getCurrency(),
                TransactionType.LOCAL_TRANSFER
        );

        transaction.setDescription("Local transfer to " + toAccount.getAccountNumber());

        return transaction;
    }

    private Transaction performForexTransfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {
        // Get exchange rate
        BigDecimal exchangeRate = forexService.getExchangeRate(
                fromAccount.getCurrency(),
                toAccount.getCurrency()
        );

        BigDecimal convertedAmount = amount.multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        fromAccount.withdraw(amount);
        toAccount.deposit(convertedAmount);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                amount,
                fromAccount.getCurrency(),
                toAccount.getCurrency(),
                TransactionType.FOREX_TRANSFER
        );

        transaction.setExchangeRate(exchangeRate);
        transaction.setDescription(String.format(
                "Forex transfer: %s %s -> %s %s (Rate: %s)",
                amount, fromAccount.getCurrency(),
                convertedAmount, toAccount.getCurrency(),
                exchangeRate
        ));

        return transaction;
    }
}