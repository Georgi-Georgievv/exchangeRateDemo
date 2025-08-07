package com.example.demo.service;

import com.example.demo.client.ExchangeRateClient;
import com.example.demo.dto.ExchangeRateResponse;
import com.example.demo.excepitons.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateClient client;

    @InjectMocks
    private ExchangeRateService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getExchangeRate_validCurrencies_returnsResponse() {
        when(client.getExchangeRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(0.85));

        ExchangeRateResponse response = service.getExchangeRate("USD", "EUR");

        assertEquals("USD", response.getFromCurrency());
        assertEquals("EUR", response.getToCurrency());
        assertEquals(BigDecimal.valueOf(0.85), response.getRate());
    }

    @Test
    void getExchangeRate_sameCurrency_returnsOne() {
        ExchangeRateResponse response = service.getExchangeRate("USD", "usd");
        assertEquals(BigDecimal.ONE, response.getRate());
    }

    @Test
    void getExchangeRate_nullFrom_throwsException() {
        assertThrows(InvalidRequestException.class,
                () -> service.getExchangeRate(null, "EUR"));
    }

    @Test
    void getExchangeRate_blankTo_throwsException() {
        assertThrows(InvalidRequestException.class,
                () -> service.getExchangeRate("USD", "  "));
    }
}
