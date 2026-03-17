package ru.fastdelivery.usecase;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.domain.common.geo.GeoPropertiesProvider;
import ru.fastdelivery.domain.common.geo.Point;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Named
@RequiredArgsConstructor
public class TariffCalculateUseCase {
    private final WeightPriceProvider weightPriceProvider;
    private final VolumePriceProvider volumePriceProvider;
    private final GeoDistanceProvider geoDistanceProvider;
    private final GeoPropertiesProvider geoPropertiesProvider;

    public Price calc(Shipment shipment) {
//        var weightAllPackagesKg = shipment.weightAllPackages().kilograms();
//        var minimalPrice = weightPriceProvider.minimalPrice();
//
//        return weightPriceProvider
//                .costPerKg()
//                .multiply(weightAllPackagesKg)
//                .max(minimalPrice);

        // Базовая стоимость по весу
        Price weightPrice = calculateByWeight(shipment);

        // Базовая стоимость по объему (если есть габариты)
        Price volumePrice = shipment.hasDimensions()
                ? calculateByVolume(shipment)
                : weightPrice;

        // Выбираем максимальную из базовых стоимостей
        Price basePrice = weightPrice.max(volumePrice);

        // Если есть координаты, применяем расстояние
        if (shipment.departure().isPresent() && shipment.destination().isPresent()) {
            return calculateWithDistance(basePrice,
                    shipment.departure().get(),
                    shipment.destination().get());
        }

        return basePrice;

    }

    private Price calculateByWeight(Shipment shipment) {
        var weightAllPackagesKg = shipment.weightAllPackages().kilograms();
        var minimalPrice = weightPriceProvider.minimalPrice();

        return weightPriceProvider
                .costPerKg()
                .multiply(weightAllPackagesKg)
                .max(minimalPrice);
    }

    private Price calculateByVolume(Shipment shipment) {
        var totalVolume = BigDecimal.valueOf(shipment.totalVolumeCubicMeters());
        var minimalPrice = weightPriceProvider.minimalPrice();

        return volumePriceProvider
                .costPerCubicMeter()
                .multiply(totalVolume)
                .max(minimalPrice);
    }

    private Price calculateWithDistance(Price basePrice, Point departure, Point destination) {
        validateCoordinates(departure);
        validateCoordinates(destination);

        BigDecimal distance = geoDistanceProvider.calculateDistance(departure, destination);
        int baseDistance = geoPropertiesProvider.getBaseDistanceKm();

        // Расчет количества базовых расстояний (минимум 1) //!!!???
        int distanceMultiplier = Math.max(1,
                (int) Math.ceil(distance.doubleValue() / baseDistance));

        // Умножаем базовую стоимость на коэффициент расстояния //!!!???
        BigDecimal multiplier = BigDecimal.valueOf(distanceMultiplier);
        Price result = basePrice.multiply(multiplier);

        // Округляем до копеек в большую сторону //!!!???
        BigDecimal roundedAmount = result.amount()
                .setScale(2, RoundingMode.CEILING);

        return new Price(roundedAmount, result.currency());
    }

    private void validateCoordinates(Point point) {
        BigDecimal lat = point.latitude();
        BigDecimal lon = point.longitude();

        if (lat.compareTo(geoPropertiesProvider.getMinLatitude()) < 0
                || lat.compareTo(geoPropertiesProvider.getMaxLatitude()) > 0) {
            throw new IllegalArgumentException(
                    "Latitude must be between " + geoPropertiesProvider.getMinLatitude()
                            + " and " + geoPropertiesProvider.getMaxLatitude());
        }

        if (lon.compareTo(geoPropertiesProvider.getMinLongitude()) < 0
                || lon.compareTo(geoPropertiesProvider.getMaxLongitude()) > 0) {
            throw new IllegalArgumentException(
                    "Longitude must be between " + geoPropertiesProvider.getMinLongitude()
                            + " and " + geoPropertiesProvider.getMaxLongitude());
        }
    }

    public Price minimalPrice() {
        return weightPriceProvider.minimalPrice();
    }
}
