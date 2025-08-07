package com.example.demo.client;

import com.example.demo.dto.fixer.FixerResponse;
import com.example.demo.excepitons.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class FixerExchangeRateClient implements ExchangeRateClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fixer.api.key}")
    private String apiKey;

    @Value("${fixer.api.url}")
    private String fixerUrl;

    @Override
    public BigDecimal getExchangeRate(String from, String to) {
        log.debug("Calling external Fixer API for exchange rate: {} → {}", from, to);

        // Fixer free tier only supports EUR as base
        String url = fixerUrl + "?access_key=" + apiKey + "&symbols=" + from + "," + to;

        try {
            FixerResponse response = restTemplate.getForObject(url, FixerResponse.class);

            if (response == null || !response.isSuccess()) {
                log.warn("Invalid Fixer API response or success=false");
                throw new ExternalServiceException("Failed to retrieve rates from Fixer API.");
            }

            BigDecimal fromRate = response.getRates().get(from);
            BigDecimal toRate = response.getRates().get(to);

            if (fromRate == null || toRate == null || fromRate.compareTo(BigDecimal.ZERO) == 0) {
                log.warn("Invalid rate data received from Fixer: fromRate={}, toRate={}", fromRate, toRate);
                throw new ExternalServiceException("Invalid currency rate data from Fixer.");
            }

            // Calculate from → to rate via EUR base
            BigDecimal exchangeRate = toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
            log.info("Exchange rate {} → {} = {}", from, to, exchangeRate);
            return exchangeRate;

        } catch (Exception e) {
            log.error("Error communicating with Fixer API", e);
            throw new ExternalServiceException("Error communicating with external exchange rate service.");
        }
    }
}
