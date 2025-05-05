package ru.innopolis.attestations.attestation03.service.simulation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;
import ru.innopolis.attestations.attestation03.model.Portfolio;
import ru.innopolis.attestations.attestation03.repository.CryptoCandleRepository;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;
import ru.innopolis.attestations.attestation03.service.strategy.DailyRebalanceStrategy;
import ru.innopolis.attestations.attestation03.service.strategy.RebalanceStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioSimulator {
    private final CryptoCandleService candleService;
    private final CryptoCandleRepository candleRepository;
    private final RebalanceStrategy rebalanceStrategy;

    /**
     * Выполняет симуляцию ребалансировки за период
     */
    public void simulate(LocalDate start, LocalDate end, Map<String, BigDecimal> weights, Portfolio portfolio) {

        // Предварительная подгрузка данных по дневным свечам с биржи.
        for (String symbol : weights.keySet()) {
            if (!symbol.equals("USDT")) {
                candleService.ensureCandlesLoaded(symbol, start, end);
            }
        }

        LocalDate current = start;

        while (!current.isAfter(end)) {
            Map<String, BigDecimal> closingPrices = new HashMap<>();

            for (String symbol : weights.keySet()) {
                Optional<CryptoCandle> candleOpt = candleRepository.findBySymbolAndDate(symbol, current);
                candleOpt.ifPresent(candle -> closingPrices.put(symbol, candle.getClose()));
            }

            // Добавляем цену USDT вручную, если он есть в стратегии
            if (weights.containsKey("USDT")) {
                closingPrices.put("USDT", BigDecimal.ONE);
            }

            if (closingPrices.size() == weights.size()) {
                rebalanceStrategy.rebalance(portfolio, current, closingPrices, weights);

                BigDecimal total = portfolio.getTotalValue(closingPrices);
                log.info("Дата: {} | Портфель: {} | Общая стоимость: {}", current, portfolio.getHoldings(), total);
            } else {
                log.warn("Пропуск {}: не хватает котировок для всех активов", current);
            }

            current = current.plusDays(1);
        }
    }
}
