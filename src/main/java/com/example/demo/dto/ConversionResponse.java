package com.example.demo.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ConversionResponse {
    private UUID transactionID;
    private BigDecimal convertedAmount;

    public ConversionResponse (UUID transactionID, BigDecimal convertedAmount){
        this.transactionID = transactionID;
        this.convertedAmount = convertedAmount;
    }
}
