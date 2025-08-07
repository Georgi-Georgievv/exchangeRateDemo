package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConversionRequest {
    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String sourceCurrency;

    @NotBlank
    private String targetCurrency;
}
