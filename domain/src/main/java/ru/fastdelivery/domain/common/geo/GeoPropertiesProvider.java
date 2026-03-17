package ru.fastdelivery.domain.common.geo;

import java.math.BigDecimal;

public interface GeoPropertiesProvider {
    BigDecimal getMinLatitude();
    BigDecimal getMaxLatitude();
    BigDecimal getMinLongitude();
    BigDecimal getMaxLongitude();
    int getBaseDistanceKm();
}
