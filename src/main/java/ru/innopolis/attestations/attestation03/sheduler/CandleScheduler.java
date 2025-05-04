package ru.innopolis.attestations.attestation03.sheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.innopolis.attestations.attestation03.client.BinanceClient;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class CandleScheduler {

    private final BinanceClient binanceClient;
    private final CryptoCandleService candleService;

    public CandleScheduler(BinanceClient binanceClient, CryptoCandleService candleService) {
        this.binanceClient = binanceClient;
        this.candleService = candleService;
    }

    @Scheduled(cron = "0 0 2 * * *") // каждый день в 2:00 ночи
    public void fetchAndStoreDailyCandles() {
        String[] symbols = {"BTCUSDT", "ETHUSDT"};
        LocalDate start = LocalDate.of(2021,01,01);
        LocalDate end = LocalDate.of(2022,01,01);
        for (String symbol : symbols) {
            try {
                log.info("Загрузка истории по {}, StartDate {}, EndDate {}", symbol, start, end);
                List<CryptoCandle> history = binanceClient.getDailyCandles(symbol, start, end);
                log.info("Получено {} свечей для {}", history.size(), symbol);

                candleService.saveAll(history);
                log.info("Сохранили котировки для {}", symbol);

            } catch (Exception e) {
                log.error("Ошибка при загрузке котировок для {}: {}", symbol, e.getMessage(), e);
            }
        }

        log.info("Шедулер завершён");
    }
}
