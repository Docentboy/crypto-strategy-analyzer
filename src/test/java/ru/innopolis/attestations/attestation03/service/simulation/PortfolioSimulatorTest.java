package ru.innopolis.attestations.attestation03.service.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;
import ru.innopolis.attestations.attestation03.model.Portfolio;
import ru.innopolis.attestations.attestation03.repository.CryptoCandleRepository;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;
import ru.innopolis.attestations.attestation03.service.strategy.DailyRebalanceStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PortfolioSimulatorTest {

    @Mock
    private CryptoCandleRepository candleRepository;

    @Mock
    private CryptoCandleService candleService;

    private PortfolioSimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new PortfolioSimulator(candleService, candleRepository, new DailyRebalanceStrategy());

        Mockito.when(candleRepository.findBySymbolAndDate(eq("BTCUSDT"), any()))
                .thenReturn(Optional.of(new CryptoCandle(
                        null, "BTCUSDT", LocalDate.of(2025, 1, 1),
                        BigDecimal.valueOf(10000),
                        BigDecimal.valueOf(10100),
                        BigDecimal.valueOf(9900),
                        BigDecimal.valueOf(10000),
                        BigDecimal.TEN
                )));
    }

    @Test
    void testSimulateWithMockedRepo() {
        Map<String, BigDecimal> weights = Map.of("BTCUSDT", BigDecimal.ONE);

        Portfolio portfolio = new Portfolio();
        portfolio.setUsd(new BigDecimal("1000"));

        simulator.simulate(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1),
                weights,
                portfolio
        );

        assertTrue(portfolio.getHoldings().get("BTCUSDT").compareTo(BigDecimal.ZERO) > 0);
    }
}