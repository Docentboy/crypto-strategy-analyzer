package ru.innopolis.attestations.attestation03.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.innopolis.attestations.attestation03.dto.ErrorResponse;
import ru.innopolis.attestations.attestation03.dto.SimulationRequest;
import ru.innopolis.attestations.attestation03.dto.SimulationResponse;
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

    @Operation(summary = "Симуляция стратегии ребалансировки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная симуляция",
                    content = @Content(schema = @Schema(implementation = SimulationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> simulate(@Valid @RequestBody SimulationRequest request) {

        log.info("simulate request: {}", request);
        // Проверка: сумма весов должна быть == 1.0
        BigDecimal weightSum = request.getWeights().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (weightSum.compareTo(BigDecimal.ONE) != 0) {
            log.info("Ошибка входных данных, сумма весов неверна {}", weightSum);
            ErrorResponse error = new ErrorResponse();
            error.setError("Сумма всех весов должна быть равна 1.0 (100%)");
            return ResponseEntity.badRequest().body(error);
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
        BigDecimal totalProfit = total.subtract(initial);
        BigDecimal totalProfitPercent = calculatePercentageChange(total, initial);

        //Высчитываем профит по каждому инструменту
        Map<String, BigDecimal> profits = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : firstDayPortfolio.getHoldings().entrySet()) {
            String symbol = entry.getKey();
            BigDecimal startAmount = entry.getValue();
            BigDecimal finalAmount = finalPortfolio.getHoldings().getOrDefault(symbol, BigDecimal.ZERO);

            if (startAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentChange = calculatePercentageChange(finalAmount, startAmount);
                profits.put(symbol, percentChange);
            } else {
                profits.put(symbol, BigDecimal.ZERO); // или null, если символ появился позже
            }
        }

        SimulationResponse response = new SimulationResponse();
        response.setFinalHoldings(finalPortfolio.getHoldings());
        response.setStartHoldings(firstDayPortfolio.getHoldings());
        response.setProfitsInPercent(profits);
        response.setTotalProfitUSD(totalProfit);
        response.setTotalProfitPercentUSD(totalProfitPercent);
        response.setTotalValueUSD(total);

        return ResponseEntity.ok(response);

    }

    private BigDecimal calculatePercentageChange(BigDecimal finalValue, BigDecimal initialValue) {
        if (initialValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return finalValue
                .divide(initialValue, 6, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}