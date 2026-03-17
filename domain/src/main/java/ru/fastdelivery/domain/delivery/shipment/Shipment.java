package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.geo.Point;
import ru.fastdelivery.domain.common.length.OuterDimensions;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;

import java.util.List;
import java.util.Optional;

/**
 * @param packages упаковки в грузе
 * @param currency валюта объявленная для груза
 * @param departure точка отправления (опционально)
 * @param destination точка назначения (опционально)
 */
public record Shipment(
        List<Pack> packages,
        Currency currency,
        Optional<Point> departure,
        Optional<Point> destination
) {

    public Shipment(List<Pack> packages, Currency currency) {
        this(packages, currency, Optional.empty(), Optional.empty());
    }

    public Shipment(List<Pack> packages, Currency currency, Point departure, Point destination) {
        this(packages, currency, Optional.ofNullable(departure), Optional.ofNullable(destination));
    }

    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::weight)
                .reduce(Weight.zero(), Weight::add);
    }

    public boolean hasDimensions() {
        return packages.stream()
                .allMatch(p -> p.dimensions().isPresent());
    }

    public double totalVolumeCubicMeters() {
        return packages.stream()
                .map(Pack::dimensions)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToDouble(OuterDimensions::volumeCubicMeters)
                .sum();
    }

}
