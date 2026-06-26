package com.zuvomo.signals.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BinancePriceClient {

    private final RestClient restClient;
    private final String baseUrl;

    public BinancePriceClient(RestClient restClient, @Value("${binance.base-url}") String baseUrl) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    public BigDecimal getCurrentPrice(String symbol) {
        BinanceTickerPrice response = restClient.get()
                .uri(baseUrl + "/api/v3/ticker/price?symbol={symbol}", symbol)
                .retrieve()
                .body(BinanceTickerPrice.class);
        if (response == null || response.price() == null) {
            throw new IllegalStateException("Binance returned no price for " + symbol);
        }
        return response.price();
    }

    private record BinanceTickerPrice(String symbol, BigDecimal price) {
    }
}
