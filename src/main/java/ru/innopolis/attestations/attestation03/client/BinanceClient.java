package ru.innopolis.attestations.attestation03.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.innopolis.attestations.attestation03.exception.CandleParsingException;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class BinanceClient {

    private static final Logger log = LoggerFactory.getLogger(BinanceClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Для запроса одного дня
    public List<CryptoCandle> getDailyCandles(String symbol, int limit) {
        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.binance.com")
                .path("/api/v3/klines")
                .queryParam("symbol", symbol)
                .queryParam("interval", "1d")
                .queryParam("limit", limit)
                .build()
                .toUriString();

        List<List<Object>> response = restTemplate.getForObject(uri, List.class);
        List<CryptoCandle> candles = new ArrayList<>();

        if (response != null) {
            for (List<Object> entry : response) {
                long openTime = ((Number) entry.get(0)).longValue();
                BigDecimal open = new BigDecimal((String) entry.get(1));
                BigDecimal high = new BigDecimal((String) entry.get(2));
                BigDecimal low = new BigDecimal((String) entry.get(3));
                BigDecimal close = new BigDecimal((String) entry.get(4));
                BigDecimal volume = new BigDecimal((String) entry.get(5));

                LocalDate date = Instant.ofEpochMilli(openTime).atZone(ZoneOffset.UTC).toLocalDate();

                candles.add(new CryptoCandle(symbol.toUpperCase(), date, open, high, low, close, volume));
            }
        }

        return candles;
    }

    // Для запроса за период времени. Возращает не более 500 записей!!!
    public List<CryptoCandle> getDailyCandles(String symbol, LocalDate startDate, LocalDate endDate) {
        long startTime = startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endTime = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=1d&startTime=%d&endTime=%d",
                symbol.toUpperCase(), startTime, endTime);

        String jsonResponse = restTemplate.getForObject(url, String.class);
        try {
            return parseJsonToCandles(jsonResponse, symbol);
        } catch (JsonProcessingException e) {
            log.error("Ошибка парсинга котировок для {}, ответ: {}", symbol, jsonResponse, e);
            throw new CandleParsingException("Не удалось распарсить свечи с Binance API", e);
        }
    }

    private List<CryptoCandle> parseJsonToCandles(String json, String symbol) throws JsonProcessingException {
        List<List<Object>> rawCandles = new ObjectMapper().readValue(json, new TypeReference<>() {});
        List<CryptoCandle> candles = new ArrayList<>();

        for (List<Object> item : rawCandles) {
            LocalDate date = Instant.ofEpochMilli(((Number) item.get(0)).longValue())
                    .atZone(ZoneOffset.UTC).toLocalDate();
            BigDecimal open = new BigDecimal(item.get(1).toString());
            BigDecimal high = new BigDecimal(item.get(2).toString());
            BigDecimal low = new BigDecimal(item.get(3).toString());
            BigDecimal close = new BigDecimal(item.get(4).toString());
            BigDecimal volume = new BigDecimal(item.get(5).toString());

            candles.add(new CryptoCandle(symbol.toUpperCase(), date, open, close, high, low, volume));
        }

        return candles;
    }
}