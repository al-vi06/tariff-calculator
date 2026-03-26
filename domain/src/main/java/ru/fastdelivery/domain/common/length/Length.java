package ru.fastdelivery.domain.common.length;

import java.math.BigInteger;
/**
 * Длина в миллиметрах
 */
public record Length(BigInteger millimeters) {

    public Length {
        if (millimeters == null || millimeters.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Length cannot be below Zero!");
        }
    }

    public static Length fromMillimeter(long mm) {
        return new Length(BigInteger.valueOf(mm));
    }

    public boolean isLongerThan(Length other) {
        return this.millimeters.compareTo(other.millimeters) > 0;
    }

    /**
     * Округление вверх кратно 50 мм
     */
    public Length roundUpTo50() {
        long mm = this.millimeters.longValue();
        long rounded = (long) Math.ceil(mm / 50.0) * 50;
        return new Length(BigInteger.valueOf(rounded));
    }
}