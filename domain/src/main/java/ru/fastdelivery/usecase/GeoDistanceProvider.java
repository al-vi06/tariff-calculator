package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.geo.Point;

import java.math.BigDecimal;

public interface GeoDistanceProvider {
    /**
     * Расчет расстояния между двумя точками в километрах
     */
    BigDecimal calculateDistance(Point departure, Point destination);

}
