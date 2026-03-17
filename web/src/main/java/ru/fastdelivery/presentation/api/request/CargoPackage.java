package ru.fastdelivery.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.math.BigInteger;

public record CargoPackage(
        @Schema(description = "Вес упаковки, граммы", example = "5667.45")
        BigInteger weight,

        @Schema(description = "Длинна упаковки, миллиметры", example = "345")
        @Min(0)
        BigInteger length,

        @Schema(description = "Ширина упаковки, миллиметры", example = "589")
        @Min(0)
        BigInteger width,

        @Schema(description = "Высота упаковки, миллиметры", example = "234")
        @Min(0)
        BigInteger height

) {
    public boolean hasDimensions() {
        return length != null && width != null && height != null;
    }
}
