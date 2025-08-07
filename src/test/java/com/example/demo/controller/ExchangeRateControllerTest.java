package com.example.demo.controller;

import com.example.demo.controllers.ExchangeRateController;
import com.example.demo.dto.ConversionRequest;
import com.example.demo.dto.ConversionResponse;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.service.ConversionService;
import com.example.demo.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        reset(exchangeRateService, conversionService);
    }

    @Test
    void getExchangeRate_returnsSuccess() throws Exception {
        when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenReturn(new ExchangeRateResponse("USD", "EUR", BigDecimal.valueOf(0.85)));

        mockMvc.perform(get("/api/exchange/rate")
                        .param("from", "USD")
                        .param("to", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.85));
    }

    @Test
    void convertCurrency_returnsSuccess() throws Exception {
        ConversionRequest request = new ConversionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        ConversionResponse response = new ConversionResponse(UUID.randomUUID(), BigDecimal.valueOf(85.00));

        when(conversionService.convertCurrency(any())).thenReturn(response);

        mockMvc.perform(post("/api/exchange/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(85.00));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        public ExchangeRateService exchangeRateService() {
            return mock(ExchangeRateService.class);
        }

        @Bean
        @Primary
        public ConversionService conversionService() {
            return mock(ConversionService.class);
        }
    }
}
