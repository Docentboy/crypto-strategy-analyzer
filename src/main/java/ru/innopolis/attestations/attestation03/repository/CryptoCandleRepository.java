package ru.innopolis.attestations.attestation03.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoCandleRepository extends JpaRepository<CryptoCandle, Long> {

    boolean existsBySymbolAndDate(String symbol, LocalDate date);

    // Найти все свечи по символу (например BTCUSDT)
    List<CryptoCandle> findBySymbol(String symbol);

    Optional<CryptoCandle> findBySymbolAndDate(String symbol, LocalDate date);

    // Найти свечи по символу в диапазоне дат
    List<CryptoCandle> findBySymbolAndDateBetween(String symbol, LocalDate startDate, LocalDate endDate);

    // Найти последнюю свечу по символу (по дате)
    CryptoCandle findTopBySymbolOrderByDateDesc(String symbol);

    List<CryptoCandle> findBySymbolOrderByDateAsc(String symbol);

    List<CryptoCandle> findBySymbolAndDateBetweenOrderByDateAsc(String symbol, LocalDate start, LocalDate end);
}
