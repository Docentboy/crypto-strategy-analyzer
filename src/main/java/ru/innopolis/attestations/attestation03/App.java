package ru.innopolis.attestations.attestation03;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.innopolis.attestations.attestation03.model.Portfolio;
import ru.innopolis.attestations.attestation03.service.simulation.PortfolioSimulator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    @Bean
    public CommandLineRunner runSimulation(PortfolioSimulator simulator) {
        return args -> {
            Map<String, BigDecimal> weights = Map.of(
                    "BTCUSDT", new BigDecimal("0.33"),
                    "ETHUSDT", new BigDecimal("0.33"),
                    "USDT", new BigDecimal("0.34")
            );

            Portfolio portfolio = new Portfolio();
            //portfolio.setUsd(new BigDecimal("1000"));
            portfolio.setUsd(BigDecimal.ZERO);
            portfolio.getHoldings().put("USDT", new BigDecimal("1000"));

            simulator.simulate(
                    LocalDate.of(2021, 1, 1),
                    LocalDate.of(2023, 1, 1),
                    weights,
                    portfolio
            );
        };
    }
}
