package ru.fastdelivery.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GeoPointRequest(
        @NotNull
        @Schema(description = "Широта", example = "55.446008")
        BigDecimal latitude,

        @NotNull
        @Schema(description = "Долгота", example = "65.339151")
        BigDecimal longitude
) {
}
