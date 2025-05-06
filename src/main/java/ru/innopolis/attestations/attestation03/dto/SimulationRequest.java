package ru.innopolis.attestations.attestation03.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Schema(description = "Запрос симуляции стратегии")
public class SimulationRequest {

    @NotNull(message = "Дата начала обязательна")
    @Schema(description = "Дата начала симуляции", example = "2024-01-01", required = true)
    private LocalDate start;

    @NotNull(message = "Дата окончания обязательна")
    @Schema(description = "Дата окончания симуляции", example = "2025-01-01", required = true)
    private LocalDate end;

    @NotNull(message = "Начальный баланс обязателен")
    @DecimalMin(value = "0.01", message = "Баланс должен быть больше нуля")
    @Schema(description = "Начальная сумма в USD", example = "1000", required = true)
    private BigDecimal initialUsd;

    @NotEmpty(message = "Нужно указать веса активов")
    @Schema(description = "Распределение долей по активам (сумма должна быть 1.0)", example = "{\"BTCUSDT\": 0.75, \"ETHUSDT\": 0.25}", required = true)
    private Map<String, @DecimalMin(value = "0.0", message = "Вес должен быть >= 0") BigDecimal> weights;
}