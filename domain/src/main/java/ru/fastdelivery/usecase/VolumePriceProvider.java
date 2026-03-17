package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.price.Price;

/**
 * Провайдер цены за объем
 */
public interface VolumePriceProvider {
    /**
     * @return стоимость за один кубический метр
     */
    Price costPerCubicMeter();
}
