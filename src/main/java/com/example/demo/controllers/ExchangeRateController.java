package com.example.demo.controllers;

import com.example.demo.dto.ConversionRequest;
import com.example.demo.dto.ConversionResponse;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.entities.ConversionTransactionEntity;
import com.example.demo.service.ConversionService;
import com.example.demo.service.ExchangeRateService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ConversionService conversionService;

    public ExchangeRateController(ExchangeRateService exchangeRateService, ConversionService conversionService){
        this.exchangeRateService = exchangeRateService;
        this.conversionService = conversionService;
    }

    /**
     * Returns the exchange rate between two currencies.
     */
    @GetMapping("/rate")
    public ExchangeRateResponse getExchangeRate(@RequestParam String from, @RequestParam String to) {
        return exchangeRateService.getExchangeRate(from, to);
    }

    /**
     * Converts an amount from one currency to another.
     */
    @PostMapping("/convert")
    public ConversionResponse convert(@RequestBody @Valid ConversionRequest request) {
        return conversionService.convertCurrency(request);
    }

    /**
     * Lists conversions filtered by optional transaction ID and date.
     */
    @GetMapping("/conversions")
    public Page<ConversionTransactionEntity> listConversions(
            @RequestParam(required = false) UUID transactionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date,
            Pageable pageable
    ) {
        return conversionService.getConversions(transactionId, date, pageable);
    }
}
