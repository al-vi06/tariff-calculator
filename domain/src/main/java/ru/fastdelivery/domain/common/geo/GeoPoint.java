package ru.fastdelivery.domain.common.geo;

import java.math.BigDecimal;

/**
 * Географическая точка (широта + долгота)
 *
 * @param latitude  широта
 * @param longitude долгота
 */
public record GeoPoint(BigDecimal latitude, BigDecimal longitude) {

    public GeoPoint {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude must not be null");
        }
    }
}