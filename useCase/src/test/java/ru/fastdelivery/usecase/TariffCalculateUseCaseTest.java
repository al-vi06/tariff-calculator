package ru.fastdelivery.usecase;

import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.geo.GeoPoint;
import ru.fastdelivery.domain.common.length.Length;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TariffCalculateUseCaseTest {
    final WeightPriceProvider weightPriceProvider = mock(WeightPriceProvider.class);
    final VolumePriceProvider volumePriceProvider = mock(VolumePriceProvider.class);
    final GeoPropertiesProvider geoPropertiesProvider = mock(GeoPropertiesProvider.class);
    final Currency currency = new CurrencyFactory(code -> true).create("RUB");

    final TariffCalculateUseCase useCase =
            new TariffCalculateUseCase(weightPriceProvider, volumePriceProvider, geoPropertiesProvider);

    void defaultGeoProps() {
        when(geoPropertiesProvider.minLatitude()).thenReturn(BigDecimal.valueOf(45));
        when(geoPropertiesProvider.maxLatitude()).thenReturn(BigDecimal.valueOf(65));
        when(geoPropertiesProvider.minLongitude()).thenReturn(BigDecimal.valueOf(30));
        when(geoPropertiesProvider.maxLongitude()).thenReturn(BigDecimal.valueOf(96));
    }

    @Test
    @DisplayName("Расчет по весу, нет габаритов, нет координат -> успешно")
    void whenNoVolumeNoDimensions_thenWeightPriceWins() {
        var minimalPrice = new Price(BigDecimal.TEN, currency);
        var pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        var pricePerM3 = new Price(BigDecimal.valueOf(3000), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerM3);

        // 1200 г = 1.2 кг * 100 = 120 руб.
        var shipment = new Shipment(
                List.of(new Pack(new Weight(BigInteger.valueOf(1200)))), currency);

        var actual = useCase.calc(shipment);

        assertThat(actual.amount()).isEqualByComparingTo(BigDecimal.valueOf(120));
    }

    @Test
    @DisplayName("Стоимость по объёму больше -> возвращается объёмная цена")
    void whenVolumeMoreExpensive_thenVolumePriceSelected() {
        var minimalPrice = new Price(BigDecimal.TEN, currency);
        var pricePerKg = new Price(BigDecimal.valueOf(1), currency); // очень дёшево по весу
        var pricePerM3 = new Price(BigDecimal.valueOf(3000), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerM3);

        // 345*589*234 → 350*600*250 = 0.0525 м³ * 3000 = 157.50
        var dims = new OuterDimensions(
                new Length(BigInteger.valueOf(345)),
                new Length(BigInteger.valueOf(589)),
                new Length(BigInteger.valueOf(234))
        );
        var pack = new Pack(new Weight(BigInteger.valueOf(100)), dims);
        var shipment = new Shipment(List.of(pack), currency);

        var actual = useCase.calc(shipment);

        assertThat(actual.amount()).isEqualByComparingTo(new BigDecimal("157.50"));
    }

    @Test
    @DisplayName("С координатами — расстояние умножает базовую цену")
    void whenCoordinatesProvided_thenDistanceApplied() {
        defaultGeoProps();
        var minimalPrice = new Price(BigDecimal.TEN, currency);
        var pricePerKg = new Price(BigDecimal.valueOf(100), currency);
        var pricePerM3 = new Price(BigDecimal.valueOf(1), currency);

        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        when(weightPriceProvider.costPerKg()).thenReturn(pricePerKg);
        when(volumePriceProvider.costPerCubicMeter()).thenReturn(pricePerM3);

        // две точки рядом (< 450 км) → шагов = 1
        var departure = new GeoPoint(BigDecimal.valueOf(55.0), BigDecimal.valueOf(65.0));
        var destination = new GeoPoint(BigDecimal.valueOf(56.0), BigDecimal.valueOf(66.0));

        var shipment = new Shipment(
                List.of(new Pack(new Weight(BigInteger.valueOf(1000)))), // 1 кг * 100 = 100 руб.
                currency, departure, destination);

        var actual = useCase.calc(shipment);

        // 1 шаг: 100 * 1 = 100 ≥ 10 → 100.00
        assertThat(actual.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("validateCoordinates — latitude вне диапазона -> исключение")
    void whenLatitudeOutOfRange_thenException() {
        defaultGeoProps();
        var point = new GeoPoint(BigDecimal.valueOf(44), BigDecimal.valueOf(60));
        assertThrows(IllegalArgumentException.class, () -> useCase.validateCoordinates(point));
    }

    @Test
    @DisplayName("validateCoordinates — longitude вне диапазона -> исключение")
    void whenLongitudeOutOfRange_thenException() {
        defaultGeoProps();
        var point = new GeoPoint(BigDecimal.valueOf(55), BigDecimal.valueOf(29));
        assertThrows(IllegalArgumentException.class, () -> useCase.validateCoordinates(point));
    }

    @Test
    @DisplayName("Получение минимальной стоимости -> успешно")
    void whenMinimalPrice_thenSuccess() {
        var minimalPrice = new Price(BigDecimal.TEN, currency);
        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);

        assertThat(useCase.minimalPrice()).isEqualTo(minimalPrice);
    }

}