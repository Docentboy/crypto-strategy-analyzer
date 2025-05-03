package ru.innopolis.attestations.attestation03.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.innopolis.attestations.attestation03.model.Portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DailyRebalanceStrategy implements RebalanceStrategy {

    @Override
    public void rebalance(Portfolio portfolio,
                          LocalDate date,
                          Map<String, BigDecimal> closingPrices,
                          Map<String, BigDecimal> targetWeights) {

        // Проверка: есть ли котировки по всем активам
        for (String symbol : targetWeights.keySet()) {
            if (!closingPrices.containsKey(symbol)) {
                log.warn("Пропущена ребалансировка на {} — нет цены для {}", date, symbol);
                return;
            }
        }

        // Расчет полной стоимости портфеля
        BigDecimal totalValue = portfolio.getTotalValue(closingPrices);
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Пропущена ребалансировка на {} — портфель пуст", date);
            return;
        }

        Map<String, BigDecimal> newHoldings = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : targetWeights.entrySet()) {
            String symbol = entry.getKey();
            BigDecimal weight = entry.getValue();
            BigDecimal price = closingPrices.get(symbol);

            BigDecimal allocation = totalValue.multiply(weight); // сумма в $
            BigDecimal quantity = allocation.divide(price, 8, RoundingMode.HALF_UP); // сколько купить

            newHoldings.put(symbol, quantity);
        }

        portfolio.setHoldings(newHoldings);
        portfolio.setUsd(BigDecimal.ZERO); // всё вложено в активы
        log.info("Портфель ребалансирован на {}. Общая стоимость: {}", date, totalValue);
    }
}