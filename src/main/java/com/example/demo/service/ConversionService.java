package com.example.demo.service;

import com.example.demo.client.ExchangeRateClient;
import com.example.demo.dto.ConversionRequest;
import com.example.demo.dto.ConversionResponse;
import com.example.demo.entities.ConversionTransactionEntity;
import com.example.demo.excepitons.InvalidRequestException;
import com.example.demo.repositories.ConversionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ConversionService {

    private final ConversionRepository conversionRepository;
    private final ExchangeRateClient exchangeRateClient;

    public ConversionService(ConversionRepository conversionRepository, ExchangeRateClient exchangeRateClient) {
        this.conversionRepository = conversionRepository;
        this.exchangeRateClient = exchangeRateClient;
    }

    public ConversionResponse convertCurrency(ConversionRequest request) {
        if (request == null) {
            throw new InvalidRequestException("ConversionRequest must not be null");
        }
        if (request.getAmount() == null) {
            throw new InvalidRequestException("Amount must not be null");
        }
        if (request.getSourceCurrency() == null || request.getTargetCurrency() == null) {
            throw new InvalidRequestException("Source and target currencies must not be null");
        }

        // Negative or zero amount check
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Amount must be greater than zero");
        }

        log.info("Received conversion request: {} {} → {}", request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency());

        BigDecimal rate = exchangeRateClient.getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency());
        BigDecimal convertedAmount = request.getAmount().multiply(rate);

        log.debug("Exchange rate: {} → {} = {}", request.getSourceCurrency(), request.getTargetCurrency(), rate);
        log.info("Converted amount: {}", convertedAmount);

        ConversionTransactionEntity tx = new ConversionTransactionEntity();
        tx.setSourceAmount(request.getAmount());
        tx.setTargetAmount(convertedAmount);
        tx.setSourceCurrency(request.getSourceCurrency());
        tx.setTargetCurrency(request.getTargetCurrency());
        tx.setTimeStamp(LocalDateTime.now());

        conversionRepository.save(tx);

        return new ConversionResponse(tx.getId(), convertedAmount);
    }

    public Page<ConversionTransactionEntity> getConversions(UUID id, LocalDateTime date, Pageable pageable) {
        log.debug("Fetching conversions by ID: {}, Date: {}", id, date);
        if (id != null) {
            return conversionRepository.findById(id)
                    .map(tx -> new PageImpl<>(List.of(tx), pageable, 1))
                    .orElseGet(() -> {
                        Page<ConversionTransactionEntity> emptyPage = Page.empty(pageable);
                        return (PageImpl<ConversionTransactionEntity>) emptyPage;
                    });
        } else if (date != null) {
            LocalDateTime start = date;
            LocalDateTime end = start.plusDays(1);
            return conversionRepository.findByTimeStampBetween(start, end, pageable);
        } else {
            throw new InvalidRequestException("Either transactionId or date must be provided.");
        }
    }

}
