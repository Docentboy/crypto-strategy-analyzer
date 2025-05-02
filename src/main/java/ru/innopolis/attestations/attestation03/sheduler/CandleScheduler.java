package ru.innopolis.attestations.attestation03.sheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.innopolis.attestations.attestation03.client.BinanceClient;
import ru.innopolis.attestations.attestation03.entity.CryptoCandle;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;

import java.time.LocalDate;
import java.util.List;

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
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        for (String symbol : symbols) {
            List<CryptoCandle> history = binanceClient.getDailyCandles(symbol, start, end);
            candleService.saveAll(history);
        }
    }
}
