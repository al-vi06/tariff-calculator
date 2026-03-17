package ru.fastdelivery.domain.delivery.pack;

import ru.fastdelivery.domain.common.length.OuterDimensions;
import ru.fastdelivery.domain.common.weight.Weight;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Упаковка груза
 *
 * @param weight вес товаров в упаковке
 */
public record Pack(Weight weight, Optional<OuterDimensions> dimensions) {

    private static final Weight maxWeight = new Weight(BigInteger.valueOf(150_000));

    public Pack {
        if (weight.greaterThan(maxWeight)) {
            throw new IllegalArgumentException("Package can't be more than " + maxWeight);
        }
    }

    public Pack(Weight weight) {
        this(weight, Optional.empty());
    }

    public Pack(Weight weight, OuterDimensions dimensions) {
        this(weight, Optional.ofNullable(dimensions));
    }
}
