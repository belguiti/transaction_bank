package com.bank_fx.transaction.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "transactions")
public class TransactionMongo {

    @Id
    private String id;
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRate;
    private String type;
    private LocalDateTime timestamp;
    private String description;
    private String status;

    public TransactionMongo() {
        this.timestamp = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    // Constructor to convert from SQL Transaction
    public TransactionMongo(Transaction tx) {
        this();
        this.transactionId = tx.getTransactionId();
        this.fromAccount = tx.getFromAccount();
        this.toAccount = tx.getToAccount();
        this.amount = tx.getAmount();
        this.fromCurrency = tx.getFromCurrency().toString();
        this.toCurrency = tx.getToCurrency().toString();
        this.exchangeRate = tx.getExchangeRate();
        this.type = tx.getType().toString();
        this.description = tx.getDescription();
        this.status = tx.getStatus().toString();
    }

    // Getters and setters...
}
