package ru.innopolis.attestations.attestation03.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortfolioTest {

    @Test
    void testTotalValue() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUsd(new BigDecimal("500"));
        portfolio.getHoldings().put("BTCUSDT", new BigDecimal("0.01"));
        portfolio.getHoldings().put("ETHUSDT", new BigDecimal("0.02"));

        Map<String, BigDecimal> prices = new HashMap<>();
        prices.put("BTCUSDT", new BigDecimal("30000"));
        prices.put("ETHUSDT", new BigDecimal("2000"));
        prices.put("USDT", BigDecimal.ONE); // опционально

        BigDecimal total = portfolio.getTotalValue(prices);

        // 500 (USDT) + 0.01 * 30000 = 300 + 0.02 * 2000 = 40 → итого 840
        assertEquals(new BigDecimal("840.00"), total.setScale(2));
    }
}
