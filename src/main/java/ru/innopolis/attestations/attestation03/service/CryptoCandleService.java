package ru.innopolis.attestations.attestation03.service;

import ru.innopolis.attestations.attestation03.model.CryptoCandle;

import java.time.LocalDate;
import java.util.List;

public interface CryptoCandleService {

    List<CryptoCandle> getCandles(String symbol);

    void saveAll(List<CryptoCandle> candles);

    /**
     * Проверяет наличие свечей в БД по годам и подгружает недостающие года.
     *
     * @param symbols список символов (например BTCUSDT, ETHUSDT)
     * @param start   дата начала периода
     * @param end     дата окончания периода
     */
    void ensureCandlesLoaded(String symbols, LocalDate start, LocalDate end);

}
