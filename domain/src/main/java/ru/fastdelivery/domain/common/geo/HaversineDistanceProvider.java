package ru.fastdelivery.domain.common.geo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Реализация расчета расстояния по формуле гаверсинусов
 */
public class HaversineDistanceProvider implements GeoDistanceProvider {
    private static final double EARTH_RADIUS = 6371.0; // Радиус Земли в км

    @Override
    public BigDecimal calculateDistance(Point departure, Point destination) {
        double lat1 = Math.toRadians(departure.latitude().doubleValue());
        double lon1 = Math.toRadians(departure.longitude().doubleValue());
        double lat2 = Math.toRadians(destination.latitude().doubleValue());
        double lon2 = Math.toRadians(destination.longitude().doubleValue());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double sinDLat = Math.sin(dlat / 2);
        double sinDLon = Math.sin(dlon / 2);

        double a = sinDLat * sinDLat
                + Math.cos(lat1) * Math.cos(lat2) * sinDLon * sinDLon;

        double sqrtA = Math.sqrt(Math.min(1, a));
        double sqrt1MinusA = Math.sqrt(Math.max(0, 1 - a));

        double c = 2 * Math.atan2(sqrtA, sqrt1MinusA);

        double distance = EARTH_RADIUS * c;

        return BigDecimal.valueOf(distance)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
