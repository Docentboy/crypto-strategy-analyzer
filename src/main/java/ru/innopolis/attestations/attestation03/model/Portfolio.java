package ru.innopolis.attestations.attestation03.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Data
public class Portfolio {

    // Остаток в USD
    private BigDecimal usd = BigDecimal.ZERO;

    // Криптовалютные активы: symbol -> количество
    private Map<String, BigDecimal> holdings = new HashMap<>();

    /**
     * Возвращает общее количество монет по символу, или 0, если нет
     */
    public BigDecimal getCrypto(String symbol) {
        return holdings.getOrDefault(symbol, BigDecimal.ZERO);
    }

    /**
     * Устанавливает количество монет по символу
     */
    public void setCrypto(String symbol, BigDecimal amount) {
        holdings.put(symbol, amount);
    }

    /**
     * Считает общую стоимость портфеля по текущим ценам
     */
    public BigDecimal getTotalValue(Map<String, BigDecimal> closingPrices) {
        BigDecimal total = usd;

        for (Map.Entry<String, BigDecimal> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            BigDecimal quantity = entry.getValue();
            BigDecimal price = closingPrices.getOrDefault(symbol, BigDecimal.ZERO);

            total = total.add(quantity.multiply(price));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}