package com.bank_fx.transaction.application.service;


import com.bank_fx.transaction.domain.model.*;
import com.bank_fx.transaction.application.annotation.TransactionLog;
import com.bank_fx.transaction.application.dto.TransactionRequest;
import com.bank_fx.transaction.application.exception.InsufficientBalanceException;
import com.bank_fx.transaction.application.exception.UserBlockedException;
import com.bank_fx.transaction.domain.repository.TransactionMongoRepository;
import com.bank_fx.transaction.domain.repository.UserRepository;
import com.bank_fx.transaction.domain.repository.BankAccountRepository;
import com.bank_fx.transaction.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMongoRepository mongoRepository; // MongoDB
    @Value("${mongo.enabled:false}")
    private boolean mongoEnabled;
    private final ForexService forexService;

    public TransactionService(UserRepository userRepository,
                              BankAccountRepository bankAccountRepository,
                              TransactionRepository transactionRepository, TransactionMongoRepository mongoRepository,
                              ForexService forexService) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.mongoRepository = mongoRepository;
        this.forexService = forexService;
    }
    public List<TransactionMongo> getTransactionsMongo(String accountNumber) {
        return mongoRepository.findByFromAccountOrToAccount(accountNumber, accountNumber);
    }
    @TransactionLog
    public Transaction transfer(TransactionRequest request) {
        // 1️⃣ Find sender and receiver users
        User fromUser = userRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        User toUser = userRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        // 2️⃣ Check if either user is blocked
        if (fromUser.isBlocked() || toUser.isBlocked()) {
            throw new UserBlockedException("One or both users are blocked");
        }

        BankAccount fromAccount = fromUser.getBankAccount();
        BankAccount toAccount = toUser.getBankAccount();

        // 3️⃣ Check balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // 4️⃣ Perform transfer
        Transaction transaction = (fromAccount.getCurrency() == toAccount.getCurrency()) ?
                performLocalTransfer(fromAccount, toAccount, request.getAmount()) :
                performForexTransfer(fromAccount, toAccount, request.getAmount());

        // 5️⃣ Save to SQL
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 6️⃣ Save to MongoDB if enabled
        if (mongoEnabled) {
            mongoRepository.save(new TransactionMongo(savedTransaction));
        }


        return savedTransaction;
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