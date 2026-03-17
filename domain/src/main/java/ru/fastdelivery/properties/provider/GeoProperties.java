package ru.fastdelivery.properties.provider;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.fastdelivery.domain.common.geo.GeoPropertiesProvider;

import java.math.BigDecimal;

@ConfigurationProperties("geo")
@Setter
public class GeoProperties implements GeoPropertiesProvider{
    private BigDecimal minLatitude;
    private BigDecimal maxLatitude;
    private BigDecimal minLongitude;
    private BigDecimal maxLongitude;
    private int baseDistanceKm;

    @Override
    public BigDecimal getMinLatitude() {
        return minLatitude;
    }

    @Override
    public BigDecimal getMaxLatitude() {
        return maxLatitude;
    }

    @Override
    public BigDecimal getMinLongitude() {
        return minLongitude;
    }

    @Override
    public BigDecimal getMaxLongitude() {
        return maxLongitude;
    }

    @Override
    public int getBaseDistanceKm() {
        return baseDistanceKm;
    }
}
