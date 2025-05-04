package ru.innopolis.attestations.attestation03.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.innopolis.attestations.attestation03.dto.SimulationRequest;
import ru.innopolis.attestations.attestation03.model.Portfolio;
import ru.innopolis.attestations.attestation03.repository.CryptoCandleRepository;
import ru.innopolis.attestations.attestation03.service.simulation.PortfolioSimulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final PortfolioSimulator simulator;
    private final CryptoCandleRepository candleRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> simulate(@Valid @RequestBody SimulationRequest request) {
        log.info("simulate request: {}", request);
        // Проверка: сумма весов должна быть == 1.0
        BigDecimal weightSum = request.getWeights().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (weightSum.compareTo(BigDecimal.ONE) != 0) {
            log.info("Ошибка входных данных, сумма весов неверна {}", weightSum);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Сумма всех весов должна быть равна 1.0 (100%)"
            ));
        }

        Portfolio finalPortfolio = new Portfolio();
        finalPortfolio.setUsd(request.getInitialUsd());
        simulator.simulate(
                request.getStart(),
                request.getEnd(),
                request.getWeights(),
                finalPortfolio
        );

        // стартовое распределение акитвов - берем симуляцияю за первый день
        Portfolio firstDayPortfolio = new Portfolio();
        firstDayPortfolio.setUsd(request.getInitialUsd());
        simulator.simulate(
                request.getStart(),
                request.getStart(), // только один день
                request.getWeights(),
                firstDayPortfolio
        );

        // Загружаем цены закрытия на дату окончания
        Map<String, BigDecimal> closingPrices = new HashMap<>();
        for (String symbol : request.getWeights().keySet()) {
            if (symbol.equals("USDT")) {
                closingPrices.put("USDT", BigDecimal.ONE);
            } else {
                candleRepository.findBySymbolAndDate(symbol, request.getEnd())
                        .ifPresent(candle -> closingPrices.put(symbol, candle.getClose()));
            }
        }
        BigDecimal initial = request.getInitialUsd();
        BigDecimal total = finalPortfolio.getTotalValue(closingPrices);
        BigDecimal totalIncome = total.subtract(initial);
        BigDecimal totalIncomePercent = total
                .divide(initial, 6, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)                   // -1
                .multiply(new BigDecimal("100"))            // *100
                .setScale(2, RoundingMode.HALF_UP);

        //Высчитываем профит по каждому инструменту
        Map<String, BigDecimal> profits = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : firstDayPortfolio.getHoldings().entrySet()) {
            String symbol = entry.getKey();
            BigDecimal startAmount = entry.getValue();
            BigDecimal finalAmount = finalPortfolio.getHoldings().getOrDefault(symbol, BigDecimal.ZERO);

            if (startAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentChange = finalAmount
                        .divide(startAmount, 6, RoundingMode.HALF_UP)
                        .subtract(BigDecimal.ONE)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
                profits.put(symbol, percentChange);
            } else {
                profits.put(symbol, BigDecimal.ZERO); // или null, если символ появился позже
            }
        }

        return ResponseEntity.ok(Map.of(
                "finalHoldings", finalPortfolio.getHoldings(),
                "startHoldings", firstDayPortfolio.getHoldings(),
                "profitsInPercent", profits,
                "totalIncomeUSD", totalIncome,
                "totalIncomePercentUSD", totalIncomePercent,
                "totalValueUSD", total
        ));


    }
}