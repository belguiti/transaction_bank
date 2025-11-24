package com.bank_fx.transaction.application.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class TransactionRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;

    // Constructors
    public TransactionRequest() {}

    public TransactionRequest(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    // Getters and Setters
    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }

    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}