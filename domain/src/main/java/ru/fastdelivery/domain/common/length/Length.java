package ru.fastdelivery.domain.common.length;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public record Length(BigInteger millimeters) implements Comparable<Length>{

    public static final Length ZERO = new Length(BigInteger.ZERO);

    public Length {
        if (millimeters == null) {
            throw new IllegalArgumentException("Length cannot be null!");
        }
        if (isLessThanZero(millimeters)) {
            throw new IllegalArgumentException("Length cannot be below Zero!");
        }
    }

    private static boolean isLessThanZero(BigInteger length) {
        return BigInteger.ZERO.compareTo(length) > 0;
    }

    public static Length fromMillimeter(long mm) {
        return new Length(BigInteger.valueOf(mm));
    }

    public static Length fromMillimeter(BigInteger mm) {
        return new Length(mm);
    }

    public boolean isLongerThan(Length other) {
        return this.millimeters.compareTo(other.millimeters) > 0;
    }

    public Length roundToNearest50() {
        BigDecimal mmDecimal = new BigDecimal(millimeters);
        BigDecimal divided = mmDecimal.divide(BigDecimal.valueOf(50), 0, RoundingMode.HALF_UP);
        BigInteger rounded = divided.multiply(BigDecimal.valueOf(50)).toBigInteger();
        return new Length(rounded);
    }

    public BigDecimal toMeters() {
        return new BigDecimal(millimeters)
                .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
    }

    @Override
    public int compareTo(Length o) {
        return this.millimeters.compareTo(o.millimeters);
    }
}
