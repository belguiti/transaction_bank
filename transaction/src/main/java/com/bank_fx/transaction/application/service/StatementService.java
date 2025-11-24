package com.bank_fx.transaction.application.service;


import com.bank_fx.transaction.domain.model.BankAccount;
import com.bank_fx.transaction.domain.model.Transaction;
import com.bank_fx.transaction.domain.repository.BankAccountRepository;
import com.bank_fx.transaction.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatementService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public StatementService(BankAccountRepository bankAccountRepository,
                            TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    public String generateTextStatement(String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(
                accountNumber, startDate, endDate);

        StringBuilder statement = new StringBuilder();
        statement.append("Bank Account Statement\n");
        statement.append("======================\n");
        statement.append("Account Number: ").append(account.getAccountNumber()).append("\n");
        statement.append("Account Holder: ").append(account.getUser().getFirstName())
                .append(" ").append(account.getUser().getLastName()).append("\n");
        statement.append("Currency: ").append(account.getCurrency()).append("\n");
        statement.append("Current Balance: ").append(account.getBalance()).append(" ")
                .append(account.getCurrency()).append("\n");
        statement.append("Statement Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");

        if (transactions.isEmpty()) {
            statement.append("No transactions found for the selected period.\n");
        } else {
            statement.append("Transactions:\n");
            statement.append("-------------\n");
            for (Transaction transaction : transactions) {
                statement.append("Date: ").append(transaction.getTimestamp()).append("\n");
                statement.append("ID: ").append(transaction.getTransactionId()).append("\n");
                statement.append("Type: ").append(transaction.getType()).append("\n");
                statement.append("Amount: ").append(transaction.getAmount()).append(" ")
                        .append(transaction.getFromCurrency()).append("\n");
                statement.append("Description: ").append(transaction.getDescription()).append("\n");
                statement.append("---\n");
            }
        }

        return statement.toString();
    }
}