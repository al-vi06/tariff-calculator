package ru.fastdelivery.domain.common.geo;

import java.math.BigDecimal;

/**
 * Географическая точка с координатами
 */
public record Point(
        BigDecimal latitude,
        BigDecimal longitude) {


    public Point {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude must be provided!");
        }
    }

    public static Point of(double lat, double lon) {
        return new Point(BigDecimal.valueOf(lat), BigDecimal.valueOf(lon));
    }
}
