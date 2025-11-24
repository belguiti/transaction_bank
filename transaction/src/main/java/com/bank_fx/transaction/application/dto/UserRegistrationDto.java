package com.bank_fx.transaction.application.dto;


import com.bank_fx.transaction.domain.model.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class UserRegistrationDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Currency currency;
    private BigDecimal initialDeposit;

    // Constructors
    public UserRegistrationDto() {}

    public UserRegistrationDto(String email, String password, String firstName, String lastName,
                               Currency currency, BigDecimal initialDeposit) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currency = currency;
        this.initialDeposit = initialDeposit;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
}