package ru.fastdelivery.domain.common.length;

/**
 * Габариты упаковки
 */
public record OuterDimensions(
        Length length,
        Length width,
        Length height
) {

    private static final Length MAX_DIMENSION = Length.fromMillimeter(1500);

    public OuterDimensions {

        if (length == null || width == null || height == null) {
            throw new IllegalArgumentException("All dimensions must be provided!");
        }

        if (width.isLongerThan(MAX_DIMENSION)) {
            throw new IllegalArgumentException("Width is longer than max allowed: " + MAX_DIMENSION.millimeters() + " mm");
        }
        if (length.isLongerThan(MAX_DIMENSION)) {
            throw new IllegalArgumentException("Length is longer than max allowed: " + MAX_DIMENSION.millimeters() + " mm");
        }
        if (height.isLongerThan(MAX_DIMENSION)) {
            throw new IllegalArgumentException("Height is longer than max allowed: " + MAX_DIMENSION.millimeters() + " mm");
        }
    }

    /**
     * Расчет объема в кубических метрах с округлением габаритов до 50 мм
     */
    public double volumeCubicMeters() {
        Length roundedLength = length.roundToNearest50();
        Length roundedWidth = width.roundToNearest50();
        Length roundedHeight = height.roundToNearest50();

        double lengthM = roundedLength.toMeters().doubleValue();
        double widthM = roundedWidth.toMeters().doubleValue();
        double heightM = roundedHeight.toMeters().doubleValue();

        double volume = lengthM * widthM * heightM;
        //до 4 знаков после запятой
        return Math.round(volume * 10000.0) / 10000.0;
    }

}
