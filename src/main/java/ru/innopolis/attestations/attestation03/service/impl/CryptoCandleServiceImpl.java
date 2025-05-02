package ru.innopolis.attestations.attestation03.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.innopolis.attestations.attestation03.entity.CryptoCandle;
import ru.innopolis.attestations.attestation03.repository.CryptoCandleRepository;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;

import java.util.List;

@Service
public class CryptoCandleServiceImpl implements CryptoCandleService {

    private final CryptoCandleRepository candleRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CryptoCandleServiceImpl(CryptoCandleRepository candleRepository, RestTemplate restTemplate) {
        this.candleRepository = candleRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void fetchAndSaveCandles(String symbol) {
        // Пример URL для API биржи
        String url = "https://api.example.com/v1/ohlcv/" + symbol + "/daily";

        // Получаем данные из API (предположим, что ответ - это список свечей)
        List<CryptoCandle> candles = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, // Параметры запроса или авторизация, если требуется
                new ParameterizedTypeReference<List<CryptoCandle>>() {
                }
        ).getBody();

        // Сохраняем данные в БД
        candleRepository.saveAll(candles);
    }

    @Override
    public List<CryptoCandle> getCandles(String symbol) {
        return candleRepository.findBySymbol(symbol);
    }

    @Override
    public void save(CryptoCandle candle) {
        candleRepository.save(candle);
    }

    @Override
    public void saveAll(List<CryptoCandle> candles) {
        List<CryptoCandle> filtered = candles.stream()
                .filter(candle -> !candleRepository.existsBySymbolAndDate(candle.getSymbol(), candle.getDate()))
                .toList();

        candleRepository.saveAll(filtered);
    }
}