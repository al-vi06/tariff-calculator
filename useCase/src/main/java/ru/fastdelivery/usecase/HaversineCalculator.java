package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.geo.GeoPoint;
/**
 * Расчёт расстояния между двумя точками по формуле гаверсинусов
 * с модификацией для антиподов.
 */
public final class HaversineCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private HaversineCalculator() {}

    public static double distanceKm(GeoPoint a, GeoPoint b) {
        double lat1 = Math.toRadians(a.latitude().doubleValue());
        double lat2 = Math.toRadians(b.latitude().doubleValue());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(
                b.longitude().doubleValue() - a.longitude().doubleValue());

        double sinDLat = Math.sin(dLat / 2);
        double sinDLon = Math.sin(dLon / 2);

        double h = sinDLat * sinDLat
                + Math.cos(lat1) * Math.cos(lat2) * sinDLon * sinDLon;

        // модификация для антиподов: clamp в [0,1]
        h = Math.min(h, 1.0);

        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }
}