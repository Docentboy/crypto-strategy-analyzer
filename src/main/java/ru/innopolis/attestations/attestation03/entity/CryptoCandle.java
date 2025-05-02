package ru.innopolis.attestations.attestation03.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "crypto_candles", uniqueConstraints = {@UniqueConstraint(columnNames = {"symbol", "date"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoCandle {

    public CryptoCandle(String symbol, LocalDate date, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol; // Название пары, например BTCUSDT

    @Column(nullable = false)
    private LocalDate date; // Дата свечи

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal open;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal close;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal high;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal low;

    @Column(precision = 19, scale = 8)
    private BigDecimal volume;


}
