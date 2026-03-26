package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.geo.GeoPoint;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;

import java.util.List;

/**
 * @param packages    упаковки в грузе
 * @param currency    валюта объявленная для груза
 * @param departure   точка отправления (опционально)
 * @param destination точка назначения  (опционально)
 */
public record Shipment(
        List<Pack> packages,
        Currency currency,
        GeoPoint departure,
        GeoPoint destination
) {
    /** Конструктор без координат (обратная совместимость) */
    public Shipment(List<Pack> packages, Currency currency) {
        this(packages, currency, null, null);
    }

    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::weight)
                .reduce(Weight.zero(), Weight::add);
    }

    public boolean hasCoordinates() {
        return departure != null && destination != null;
    }
}