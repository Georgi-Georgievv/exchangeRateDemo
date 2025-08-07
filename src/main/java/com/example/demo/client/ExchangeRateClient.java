package com.example.demo.client;

import com.example.demo.excepitons.ExternalServiceException;

import java.math.BigDecimal;

public interface ExchangeRateClient {
    BigDecimal getExchangeRate(String from, String to) throws ExternalServiceException;
}
