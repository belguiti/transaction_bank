package com.bank_fx.transaction.application.service;


import com.bank_fx.transaction.domain.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ForexService {

    private final Map<String, BigDecimal> exchangeRates = new HashMap<>();

    public ForexService() {
        // Initialize with some sample exchange rates
        exchangeRates.put("USD_EUR", new BigDecimal("0.85"));
        exchangeRates.put("EUR_USD", new BigDecimal("1.18"));
        exchangeRates.put("USD_GBP", new BigDecimal("0.73"));
        exchangeRates.put("GBP_USD", new BigDecimal("1.37"));
        exchangeRates.put("USD_JPY", new BigDecimal("110.50"));
        exchangeRates.put("JPY_USD", new BigDecimal("0.0090"));
    }

    public BigDecimal getExchangeRate(Currency from, Currency to) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        String key = from.name() + "_" + to.name();
        BigDecimal rate = exchangeRates.get(key);

        if (rate == null) {
            throw new RuntimeException("Exchange rate not available for " + key);
        }

        return rate;
    }
}