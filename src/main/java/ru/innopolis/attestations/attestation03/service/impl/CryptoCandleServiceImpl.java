package ru.innopolis.attestations.attestation03.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.innopolis.attestations.attestation03.client.BinanceClient;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;
import ru.innopolis.attestations.attestation03.repository.CryptoCandleRepository;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CryptoCandleServiceImpl implements CryptoCandleService {

    private final CryptoCandleRepository candleRepository;
    private final RestTemplate restTemplate;
    private final BinanceClient binanceClient;

    @Autowired
    public CryptoCandleServiceImpl(CryptoCandleRepository candleRepository, RestTemplate restTemplate, BinanceClient binanceClient) {
        this.candleRepository = candleRepository;
        this.restTemplate = restTemplate;
        this.binanceClient = binanceClient;
    }

    @Override
    public List<CryptoCandle> getCandles(String symbol) {
        return candleRepository.findBySymbol(symbol);
    }

    @Override
    public void saveAll(List<CryptoCandle> candles) {
        List<CryptoCandle> filtered = candles.stream()
                .filter(candle -> !candleRepository.existsBySymbolAndDate(candle.getSymbol(), candle.getDate()))
                .toList();

        candleRepository.saveAll(filtered);
    }

    /* Загружаем дневные свечи за отсутсвующие годы */
    @Override
    public void ensureCandlesLoaded(String symbol, LocalDate startDate, LocalDate endDate) {
        List<Integer> requestedYears = getYearsInRange(startDate, endDate);
        Set<Integer> existingYears = getAvailableYears(symbol);

        for (Integer year : requestedYears) {
            if (!existingYears.contains(year)) {
                LocalDate from = LocalDate.of(year, 1, 1);
                LocalDate to = LocalDate.of(year, 12, 31);
                List<CryptoCandle> candles = binanceClient.getDailyCandles(symbol, from, to);
                saveAll(candles);
                log.info("Загружены котировки за {} год для {}", year, symbol);
            }
        }
    }

    private List<Integer> getYearsInRange(LocalDate start, LocalDate end) {
        return IntStream.rangeClosed(start.getYear(), end.getYear())
                .boxed()
                .collect(Collectors.toList());
    }

    private Set<Integer> getAvailableYears(String symbol) {
        return candleRepository.findDistinctYearsBySymbol(symbol);
    }
}