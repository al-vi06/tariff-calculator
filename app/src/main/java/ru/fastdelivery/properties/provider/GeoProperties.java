package ru.fastdelivery.properties.provider;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.fastdelivery.usecase.GeoPropertiesProvider;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties("geo")
@Setter
public class GeoProperties implements GeoPropertiesProvider{
    private BigDecimal minLatitude;
    private BigDecimal maxLatitude;
    private BigDecimal minLongitude;
    private BigDecimal maxLongitude;

    @Override public BigDecimal minLatitude()  { return minLatitude; }
    @Override public BigDecimal maxLatitude()  { return maxLatitude; }
    @Override public BigDecimal minLongitude() { return minLongitude; }
    @Override public BigDecimal maxLongitude() { return maxLongitude; }
}
