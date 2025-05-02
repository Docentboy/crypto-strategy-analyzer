package ru.innopolis.attestations.attestation03.service;

import ru.innopolis.attestations.attestation03.entity.CryptoCandle;

import java.util.List;

public interface CryptoCandleService {

    void fetchAndSaveCandles(String symbol);

    List<CryptoCandle> getCandles(String symbol);

    void save(CryptoCandle candle);

    void saveAll(List<CryptoCandle> candles);
}
