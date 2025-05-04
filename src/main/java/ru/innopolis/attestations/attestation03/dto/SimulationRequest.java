package ru.innopolis.attestations.attestation03.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class SimulationRequest {

    @NotNull(message = "Дата начала обязательна")
    private LocalDate start;

    @NotNull(message = "Дата окончания обязательна")
    private LocalDate end;

    @NotNull(message = "Начальный баланс обязателен")
    @DecimalMin(value = "0.01", message = "Баланс должен быть больше нуля")
    private BigDecimal initialUsd;

    @NotEmpty(message = "Нужно указать веса активов")
    private Map<String, @DecimalMin(value = "0.0", message = "Вес должен быть >= 0") BigDecimal> weights;
}