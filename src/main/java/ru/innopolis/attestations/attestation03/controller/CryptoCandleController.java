package ru.innopolis.attestations.attestation03.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.innopolis.attestations.attestation03.service.CryptoCandleService;

@RestController
public class CryptoCandleController {

    private final CryptoCandleService cryptoCandleService;

    @Autowired
    public CryptoCandleController(CryptoCandleService cryptoCandleService) {
        this.cryptoCandleService = cryptoCandleService;
    }

    @GetMapping("/fetch-candles")
    public String fetchCandles(@RequestParam String symbol) {
        cryptoCandleService.fetchAndSaveCandles(symbol);
        return "Candles for " + symbol + " fetched and saved!";
    }
}
