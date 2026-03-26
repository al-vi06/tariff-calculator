package ru.fastdelivery.usecase;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.domain.common.geo.GeoPoint;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Named
@RequiredArgsConstructor
public class TariffCalculateUseCase {

    private static final BigDecimal DISTANCE_STEP_KM = BigDecimal.valueOf(450);

    private final WeightPriceProvider weightPriceProvider;
    private final VolumePriceProvider volumePriceProvider;
    private final GeoPropertiesProvider geoPropertiesProvider;

    public Price calc(Shipment shipment) {
        // 1. Базовая цена = max(по весу, по объёму)
        Price basePrice = calcByWeight(shipment).max(calcByVolume(shipment));

        // 2. Если есть координаты — умножаем на расстояние
        if (shipment.hasCoordinates()) {
            basePrice = applyDistance(basePrice, shipment.departure(), shipment.destination());
        }

        return basePrice.max(weightPriceProvider.minimalPrice());
    }

    public Price minimalPrice() {
        return weightPriceProvider.minimalPrice();
    }

    private Price calcByWeight(Shipment shipment) {
        var weightKg = shipment.weightAllPackages().kilograms();
        return weightPriceProvider.costPerKg().multiply(weightKg);
    }

    private Price calcByVolume(Shipment shipment) {
        BigDecimal totalVolume = shipment.packages().stream()
                .filter(Pack::hasDimensions)
                .map(p -> p.dimensions().volumeInCubicMeters())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return volumePriceProvider.costPerCubicMeter().multiply(totalVolume);
    }

    /**
     * За каждые 450 км взимается базовая стоимость (не менее одного шага).
     * Результат округляется до копейки вверх.
     */
    private Price applyDistance(Price basePrice, GeoPoint departure, GeoPoint destination) {
        double distanceKm = HaversineCalculator.distanceKm(departure, destination);
        BigDecimal distance = BigDecimal.valueOf(distanceKm);

        // ceil(distance / 450), минимум 1
        BigDecimal steps = distance.divide(DISTANCE_STEP_KM, 10, RoundingMode.HALF_UP);
        long stepsLong = (long) Math.ceil(steps.doubleValue());
        if (stepsLong < 1) stepsLong = 1;

        BigDecimal multiplied = basePrice.amount()
                .multiply(BigDecimal.valueOf(stepsLong))
                .setScale(2, RoundingMode.CEILING);

        return new Price(multiplied, basePrice.currency());
    }

    public void validateCoordinates(GeoPoint point) {
        BigDecimal lat = point.latitude();
        BigDecimal lon = point.longitude();

        if (lat.compareTo(geoPropertiesProvider.minLatitude()) < 0
                || lat.compareTo(geoPropertiesProvider.maxLatitude()) > 0) {
            throw new IllegalArgumentException(
                    "Latitude must be between " + geoPropertiesProvider.minLatitude()
                            + " and " + geoPropertiesProvider.maxLatitude());
        }
        if (lon.compareTo(geoPropertiesProvider.minLongitude()) < 0
                || lon.compareTo(geoPropertiesProvider.maxLongitude()) > 0) {
            throw new IllegalArgumentException(
                    "Longitude must be between " + geoPropertiesProvider.minLongitude()
                            + " and " + geoPropertiesProvider.maxLongitude());
        }
    }
}
