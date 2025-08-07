package com.example.demo.service;

import com.example.demo.client.ExchangeRateClient;
import com.example.demo.dto.ConversionRequest;
import com.example.demo.dto.ConversionResponse;
import com.example.demo.entities.ConversionTransactionEntity;
import com.example.demo.excepitons.InvalidRequestException;
import com.example.demo.repositories.ConversionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversionServiceTest {

    @Mock
    private ConversionRepository conversionRepository;

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void convertCurrency_validRequest_returnsResponse() {
        ConversionRequest request = new ConversionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        when(exchangeRateClient.getExchangeRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(0.85));
        when(conversionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ConversionResponse response = conversionService.convertCurrency(request);

        assertNotNull(response);
        assertNotNull(response.getTransactionID());
        assertEquals(BigDecimal.valueOf(85.00), response.getConvertedAmount());
    }

    @Test
    void convertCurrency_nullAmount_throwsException() {
        ConversionRequest request = new ConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        assertThrows(InvalidRequestException.class, () -> conversionService.convertCurrency(request));
    }

    @Test
    void getConversions_byId_returnsPage() {
        UUID id = UUID.randomUUID();
        ConversionTransactionEntity transaction = new ConversionTransactionEntity();
        transaction.setId(id);

        when(conversionRepository.findById(id)).thenReturn(Optional.of(transaction));

        Page<ConversionTransactionEntity> result = conversionService.getConversions(id, null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(id, result.getContent().get(0).getId());
    }

    @Test
    void getConversions_byDate_returnsPage() {
        LocalDateTime date = LocalDateTime.now();
        List<ConversionTransactionEntity> data = List.of(new ConversionTransactionEntity());

        when(conversionRepository.findByTimeStampBetween(any(), any(), any()))
                .thenReturn(new PageImpl<>(data));

        Page<ConversionTransactionEntity> result = conversionService.getConversions(null, date, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getConversions_invalidParams_throwsException() {
        assertThrows(InvalidRequestException.class,
                () -> conversionService.getConversions(null, null, PageRequest.of(0, 10)));
    }
}
