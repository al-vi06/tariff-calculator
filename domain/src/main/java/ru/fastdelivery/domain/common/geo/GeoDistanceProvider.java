package ru.fastdelivery.domain.common.geo;

import java.math.BigDecimal;

public interface GeoDistanceProvider {
    /**
     * Расчет расстояния между двумя точками в километрах
     */
    BigDecimal calculateDistance(Point departure, Point destination);

}
