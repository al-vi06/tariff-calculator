package ru.fastdelivery.domain.delivery.pack;
import ru.fastdelivery.domain.common.length.Length;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Габариты упаковки
 *
 * @param length Длина в мм
 * @param width  Ширина в мм
 * @param height Высота в мм
 */
public record OuterDimensions(
        Length length,
        Length width,
        Length height
) {
    private static final long MAX_LENGTH_DIMENSION_MM = 1_500L;

    public OuterDimensions {
        var maxLength = Length.fromMillimeter(MAX_LENGTH_DIMENSION_MM);
        if (length.isLongerThan(maxLength)) {
            throw new IllegalArgumentException("length is longer than " + MAX_LENGTH_DIMENSION_MM + " mm");
        }
        if (width.isLongerThan(maxLength)) {
            throw new IllegalArgumentException("width is longer than " + MAX_LENGTH_DIMENSION_MM + " mm");
        }
        if (height.isLongerThan(maxLength)) {
            throw new IllegalArgumentException("height is longer than " + MAX_LENGTH_DIMENSION_MM + " mm");
        }
    }

    /**
     * Объём в кубических метрах, до 4 знаков после запятой
     * Перед расчётом каждый габарит округляется кратно 50 мм
     */
    public BigDecimal volumeInCubicMeters() {
        long l = length.roundUpTo50().millimeters().longValue();
        long w = width.roundUpTo50().millimeters().longValue();
        long h = height.roundUpTo50().millimeters().longValue();

        // мм³ → м³: делим на 1_000_000_000
        return BigDecimal.valueOf(l)
                .multiply(BigDecimal.valueOf(w))
                .multiply(BigDecimal.valueOf(h))
                .divide(BigDecimal.valueOf(1_000_000_000L), 4, RoundingMode.HALF_UP);
    }
}