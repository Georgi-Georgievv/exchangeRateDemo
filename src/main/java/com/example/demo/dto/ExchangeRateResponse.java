package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExchangeRateResponse {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;

}
