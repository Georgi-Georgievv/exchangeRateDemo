package com.example.demo.client;

import com.example.demo.excepitons.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class FixerExchangeRateClient implements ExchangeRateClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public FixerExchangeRateClient(RestTemplate restTemplate,
                                   @Value("${fixer.api.key}") String apiKey,
                                   @Value("${fixer.api.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * Calls Fixer API to fetch exchange rate.
     */
    @Override
    public BigDecimal getExchangeRate(String from, String to) throws ExternalServiceException {
        log.debug("Calling external API for rate: {} → {}", from, to);
        if (from == null || to == null) {
            throw new ExternalServiceException("Currency codes must not be null.");
        }

        String url = String.format("%s/latest?access_key=%s&base=%s&symbols=%s", baseUrl, apiKey, from, to);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            log.debug("Fixer API response: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ExternalServiceException("Failed to retrieve exchange rate from external API.");
            }

            Map<String, Object> body = response.getBody();
            Map<String, Object> rates = (Map<String, Object>) body.get("rates");

            if (rates == null || !rates.containsKey(to)) {
                throw new ExternalServiceException("Target currency not found in response.");
            }

            Object rateValue = rates.get(to);
            if (rateValue instanceof Number) {
                log.info("Parsed rate from Fixer API: {} → {} = {}", from, to, rateValue);
                return BigDecimal.valueOf(((Number) rateValue).doubleValue());
            } else {
                throw new ExternalServiceException("Invalid rate value format.");
            }

        } catch (RestClientException e) {
            throw new ExternalServiceException("Error communicating with external exchange rate service.");
        }
    }
}
