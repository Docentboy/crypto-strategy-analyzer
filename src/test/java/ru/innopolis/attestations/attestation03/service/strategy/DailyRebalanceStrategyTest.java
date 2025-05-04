package ru.innopolis.attestations.attestation03.service.strategy;
import org.junit.jupiter.api.Test;
import ru.innopolis.attestations.attestation03.model.Portfolio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DailyRebalanceStrategyTest {

    private final RebalanceStrategy strategy = new DailyRebalanceStrategy();

    @Test
    void testRebalanceSingleAsset() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUsd(BigDecimal.valueOf(1000));

        Map<String, BigDecimal> weights = Map.of("BTCUSDT", BigDecimal.ONE);
        Map<String, BigDecimal> prices = Map.of("BTCUSDT", BigDecimal.valueOf(10000));
        LocalDate date = LocalDate.of(2025, 1, 1);

        strategy.rebalance(portfolio, date, prices, weights);

        assertThat(portfolio.getUsd()).isEqualByComparingTo("0.00");
        BigDecimal actual = portfolio.getHoldings().get("BTCUSDT");
        assertThat(actual).isEqualByComparingTo("0.1");
    }

    @Test
    void testRebalanceTwoAssets() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUsd(BigDecimal.valueOf(2000));

        Map<String, BigDecimal> weights = Map.of(
                "BTCUSDT", new BigDecimal("0.5"),
                "ETHUSDT", new BigDecimal("0.5")
        );

        Map<String, BigDecimal> prices = Map.of(
                "BTCUSDT", new BigDecimal("20000"),
                "ETHUSDT", new BigDecimal("2000")
        );

        LocalDate date = LocalDate.of(2025, 1, 1);

        strategy.rebalance(portfolio, date, prices, weights);

        assertThat(portfolio.getHoldings().get("BTCUSDT"))
                .isEqualByComparingTo(new BigDecimal("0.05")); // 1000 / 20000

        assertThat(portfolio.getHoldings().get("ETHUSDT"))
                .isEqualByComparingTo(new BigDecimal("0.5"));  // 1000 / 2000
    }

    @Test
    void testRebalanceWithMissingPrice() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUsd(BigDecimal.valueOf(1000));

        Map<String, BigDecimal> weights = Map.of("BTCUSDT", BigDecimal.ONE);
        Map<String, BigDecimal> prices = Map.of(); // пустые цены

        LocalDate date = LocalDate.of(2025, 1, 1);

        strategy.rebalance(portfolio, date, prices, weights);

        // В портфеле осталась только USD, ребаланс не произошёл
        assertThat(portfolio.getUsd()).isEqualByComparingTo("1000");
        assertThat(portfolio.getHoldings()).isEmpty();
    }

    @Test
    void testRebalanceWithZeroUsd() {
        Portfolio portfolio = new Portfolio();
        portfolio.setUsd(BigDecimal.ZERO); // нет USD

        Map<String, BigDecimal> weights = Map.of("BTCUSDT", BigDecimal.ONE);
        Map<String, BigDecimal> prices = Map.of("BTCUSDT", new BigDecimal("20000"));

        LocalDate date = LocalDate.of(2025, 1, 1);

        strategy.rebalance(portfolio, date, prices, weights);

        // Ничего не изменилось, так как нет средств
        assertThat(portfolio.getUsd()).isEqualByComparingTo("0.00");
        assertThat(portfolio.getHoldings()).isEmpty();
    }
}