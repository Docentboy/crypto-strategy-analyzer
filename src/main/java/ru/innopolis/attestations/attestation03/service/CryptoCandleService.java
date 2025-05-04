package ru.innopolis.attestations.attestation03.service;

import ru.innopolis.attestations.attestation03.model.CryptoCandle;

import java.util.List;

public interface CryptoCandleService {

    void fetchAndSaveCandles(String symbol);

    List<CryptoCandle> getCandles(String symbol);

    void saveAll(List<CryptoCandle> candles);
}
