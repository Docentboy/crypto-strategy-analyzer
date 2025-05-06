package ru.innopolis.attestations.attestation03.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "Ответ симуляции ребалансировки портфеля")
public class SimulationResponse {

    @Schema(description = "Итоговая стоимость портфеля в долларах", example = "1123.45")
    private BigDecimal totalValueUSD;
    @Schema(description = "Общий доход в долларах США", example = "123.45")
    private BigDecimal totalProfitUSD;
    @Schema(description = "Общий доход в процентах", example = "12.35")
    private BigDecimal totalProfitPercentUSD;
    @Schema(description = "Начальное распределение активов", example = "{\"BTCUSDT\": 0.05, \"ETHUSDT\": 0.25}")
    private Map<String, BigDecimal> startHoldings;
    @Schema(description = "Итоговое распределение активов", example = "{\"BTCUSDT\": 0.1, \"ETHUSDT\": 0.2}")
    private Map<String, BigDecimal> finalHoldings;
    @Schema(description = "Прибыль по каждому активу в процентах", example = "{\"BTCUSDT\": 10.5, \"ETHUSDT\": -5.3}")
    private Map<String, BigDecimal> profitsInPercent;

}
