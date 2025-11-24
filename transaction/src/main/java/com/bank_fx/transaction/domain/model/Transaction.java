package com.bank_fx.transaction.domain.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal exchangeRate;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String description;
    private TransactionStatus status;

    // Constructors
    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }

    public Transaction(String fromAccount, String toAccount, BigDecimal amount,
                       Currency fromCurrency, Currency toCurrency, TransactionType type) {
        this();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.type = type;
        this.transactionId = generateTransactionId();
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getFromAccount() { return fromAccount; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }

    public String getToAccount() { return toAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Currency getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(Currency fromCurrency) { this.fromCurrency = fromCurrency; }

    public Currency getToCurrency() { return toCurrency; }
    public void setToCurrency(Currency toCurrency) { this.toCurrency = toCurrency; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}
