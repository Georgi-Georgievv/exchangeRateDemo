package com.example.demo.service;

import com.example.demo.client.ExchangeRateClient;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.excepitons.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class ExchangeRateService {

    private final ExchangeRateClient client;

    public ExchangeRateService(ExchangeRateClient client) {
        this.client = client;
    }

    /**
     * Retrieves the exchange rate and builds a DTO response.
     * Includes basic input validation and self-conversion check.
     */
    public ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency) {
        // Null checks
        log.info("Requesting exchange rate: {} → {}", fromCurrency, toCurrency);

        if (fromCurrency == null || toCurrency == null) {
            throw new InvalidRequestException("Currency codes must not be null.");
        }

        // Blank or invalid check (trim + length check)
        if (fromCurrency.trim().isEmpty() || toCurrency.trim().isEmpty()) {
            throw new InvalidRequestException("Currency codes must not be blank.");
        }

        // Self conversion shortcut
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            log.debug("Same currency conversion detected");
            return new ExchangeRateResponse(fromCurrency.toUpperCase(), toCurrency.toUpperCase(), BigDecimal.ONE);
        }

        // Get exchange rate from client
        BigDecimal rate = client.getExchangeRate(fromCurrency, toCurrency);
        log.info("Received exchange rate: {} → {} = {}", fromCurrency, toCurrency, rate);

        // Build and return DTO
        return new ExchangeRateResponse(fromCurrency.toUpperCase(), toCurrency.toUpperCase(), rate);
    }
}
