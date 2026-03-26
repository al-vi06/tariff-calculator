package ru.fastdelivery.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigInteger;

public record CargoPackage(
        @Schema(description = "Вес упаковки, граммы", example = "5667.45")
        BigInteger weight,

        @Schema(description = "Длина упаковки, миллиметры", example = "345")
        BigInteger length,

        @Schema(description = "Ширина упаковки, миллиметры", example = "589")
        BigInteger width,

        @Schema(description = "Высота упаковки, миллиметры", example = "234")
        BigInteger height
) {
}
