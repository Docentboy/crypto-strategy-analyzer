package ru.innopolis.attestations.attestation03.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ответ об ошибке")
public class ErrorResponse {

    @Schema(description = "Сообщение об ошибке", example = "Сумма всех весов должна быть равна 1.0 (100%)")
    private String error;

}
