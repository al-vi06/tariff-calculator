package ru.fastdelivery.domain.delivery.pack;

import ru.fastdelivery.domain.common.weight.Weight;

import java.math.BigInteger;

/**
 * Упаковка груза
 *
 * @param weight     вес товаров в упаковке
 * @param dimensions габариты (могут отсутствовать)
 */
public record Pack(Weight weight, OuterDimensions dimensions) {

    private static final Weight MAX_WEIGHT = new Weight(BigInteger.valueOf(150_000));

    /** Конструктор без габаритов (обратная совместимость) */
    public Pack(Weight weight) {
        this(weight, null);
    }

    public Pack {
        if (weight.greaterThan(MAX_WEIGHT)) {
            throw new IllegalArgumentException("Package can't be more than " + MAX_WEIGHT);
        }
    }

    public boolean hasDimensions() {
        return dimensions != null;
    }
}