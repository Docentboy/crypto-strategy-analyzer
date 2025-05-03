package ru.innopolis.attestations.attestation03.service.strategy;

import ru.innopolis.attestations.attestation03.model.Portfolio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface RebalanceStrategy {

    /**
     * Ребалансирует портфель по заданным ценам и целевым весам.
     * @param portfolio текущий портфель
     * @param date дата ребалансировки
     * @param closingPrices цены закрытия на дату (symbol -> price)
     * @param targetWeights целевые веса (symbol -> 0.5, 0.25 и т.п.)
     */
    void rebalance(Portfolio portfolio,
                   LocalDate date,
                   Map<String, BigDecimal> closingPrices,
                   Map<String, BigDecimal> targetWeights);
}