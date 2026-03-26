package ru.fastdelivery.domain.delivery.pack;

import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.length.Length;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.*;

public class OuterDimensionsTest {
    @Test
    void whenAllDimensionsValid_thenCreated() {
        var dims = dims(345, 589, 234);
        assertThat(dims).isNotNull();
    }

    @Test
    void whenLengthExceedsMax_thenException() {
        assertThatThrownBy(() -> dims(1501, 100, 100))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenWidthExceedsMax_thenException() {
        assertThatThrownBy(() -> dims(100, 1501, 100))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenHeightExceedsMax_thenException() {
        assertThatThrownBy(() -> dims(100, 100, 1501))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenAnyDimensionNegative_thenException() {
        // Length сам бросает исключение при -1
        assertThatThrownBy(() -> dims(-1, 100, 100))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void volumeCalculation_example() {
        // 345 → 350, 589 → 600, 234 → 250
        // 350 * 600 * 250 / 1_000_000_000 = 52_500_000 / 1_000_000_000 = 0.0525
        var dims = dims(345, 589, 234);
        assertThat(dims.volumeInCubicMeters())
                .isEqualByComparingTo(new BigDecimal("0.0525"));
    }

    @Test
    void volumeCalculation_exactMultiples() {
        // 500 * 1000 * 250 / 1e9 = 0.125
        var dims = dims(500, 1000, 250);
        assertThat(dims.volumeInCubicMeters())
                .isEqualByComparingTo(new BigDecimal("0.1250"));
    }

    private OuterDimensions dims(long l, long w, long h) {
        return new OuterDimensions(
                new Length(BigInteger.valueOf(l)),
                new Length(BigInteger.valueOf(w)),
                new Length(BigInteger.valueOf(h))
        );
    }
}
