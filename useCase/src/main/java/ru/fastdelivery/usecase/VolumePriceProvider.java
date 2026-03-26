package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.price.Price;

public interface VolumePriceProvider {
    /** Стоимость за 1 м³ */
    Price costPerCubicMeter();
}