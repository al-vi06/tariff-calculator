package ru.fastdelivery.domain.common.length;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.*;

public class LengthTest {
    @ParameterizedTest(name = "мм = {0} -> объект создан")
    @ValueSource(longs = {0, 1, 500, 1500})
    void whenValidMm_thenCreated(long mm) {
        assertThat(new Length(BigInteger.valueOf(mm))).isNotNull();
    }

    @ParameterizedTest(name = "мм = {0} -> исключение")
    @ValueSource(longs = {-1, -100})
    void whenNegativeMm_thenException(long mm) {
        assertThatThrownBy(() -> new Length(BigInteger.valueOf(mm)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundUpTo50_exactMultiple() {
        // 350 → 350
        assertThat(new Length(BigInteger.valueOf(350)).roundUpTo50().millimeters())
                .isEqualTo(BigInteger.valueOf(350));
    }

    @Test
    void roundUpTo50_notExact() {
        // 345 → 350
        assertThat(new Length(BigInteger.valueOf(345)).roundUpTo50().millimeters())
                .isEqualTo(BigInteger.valueOf(350));
    }

    @Test
    void isLongerThan_true() {
        var big = new Length(BigInteger.valueOf(600));
        var small = new Length(BigInteger.valueOf(400));
        assertThat(big.isLongerThan(small)).isTrue();
    }

    @Test
    void isLongerThan_false() {
        var a = new Length(BigInteger.valueOf(200));
        var b = new Length(BigInteger.valueOf(200));
        assertThat(a.isLongerThan(b)).isFalse();
    }
}
