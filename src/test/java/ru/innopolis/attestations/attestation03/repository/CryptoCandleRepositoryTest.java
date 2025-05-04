package ru.innopolis.attestations.attestation03.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.innopolis.attestations.attestation03.model.CryptoCandle;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CryptoCandleRepositoryTest {

    @Autowired
    private CryptoCandleRepository repository;

    @Test
    public void testSaveAndFindBySymbolAndDate() {
        CryptoCandle candle = new CryptoCandle();
        candle.setSymbol("BTCUSDT");
        candle.setDate(LocalDate.of(2024, 1, 1));
        candle.setOpen(new BigDecimal("10000"));
        candle.setClose(new BigDecimal("10500"));
        candle.setHigh(new BigDecimal("10600"));
        candle.setLow(new BigDecimal("9900"));
        candle.setVolume(new BigDecimal("123.45"));

        repository.save(candle);

        var result = repository.findBySymbolAndDate("BTCUSDT", LocalDate.of(2024, 1, 1));

        assertThat(result).isPresent();
        assertThat(result.get().getClose()).isEqualByComparingTo("10500");
    }
}