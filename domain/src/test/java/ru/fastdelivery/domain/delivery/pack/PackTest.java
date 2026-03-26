package ru.fastdelivery.domain.delivery.pack;

import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.length.Length;
import ru.fastdelivery.domain.common.weight.Weight;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackTest {

    @Test
    void whenWeightMoreThanMaxWeight_thenThrowException() {
        var weight = new Weight(BigInteger.valueOf(150_001));
        assertThatThrownBy(() -> new Pack(weight))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenWeightLessThanMaxWeight_thenObjectCreated() {
        var actual = new Pack(new Weight(BigInteger.valueOf(1_000)));
        assertThat(actual.weight()).isEqualTo(new Weight(BigInteger.valueOf(1_000)));
    }

    @Test
    void whenPackHasDimensions_thenHasDimensionsIsTrue() {
        var dims = new OuterDimensions(
                new Length(BigInteger.valueOf(100)),
                new Length(BigInteger.valueOf(100)),
                new Length(BigInteger.valueOf(100))
        );
        var pack = new Pack(new Weight(BigInteger.valueOf(1_000)), dims);
        assertThat(pack.hasDimensions()).isTrue();
    }

    @Test
    void whenPackNoDimensions_thenHasDimensionsIsFalse() {
        var pack = new Pack(new Weight(BigInteger.valueOf(1_000)));
        assertThat(pack.hasDimensions()).isFalse();
    }

}