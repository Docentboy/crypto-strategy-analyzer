package ru.innopolis.attestations.attestation03.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.innopolis.attestations.attestation03.entity.CryptoCandle;

import java.util.List;

@Component
public class ApiClient {


    private final RestTemplate restTemplate;

    public ApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<CryptoCandle> fetchCandles(String apiUrl) throws JsonProcessingException {
        // Делаем запрос к API и получаем данные
        String response = restTemplate.getForObject(apiUrl, String.class);

        // Преобразуем JSON в объекты CryptoCandle
        List<CryptoCandle> candles = parseJsonToCandles(response);
        return candles;
    }

    private List<CryptoCandle> parseJsonToCandles(String jsonResponse) throws JsonProcessingException {
        // Преобразование JSON в объекты
        // Используй библиотеки типа Jackson или Gson для этого
        // Пример:
        return new ObjectMapper().readValue(jsonResponse, new TypeReference<List<CryptoCandle>>(){});
    }
}
