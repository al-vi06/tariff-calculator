package ru.fastdelivery.usecase;

import java.math.BigDecimal;

public interface GeoPropertiesProvider {
    BigDecimal minLatitude();
    BigDecimal maxLatitude();
    BigDecimal minLongitude();
    BigDecimal maxLongitude();
}