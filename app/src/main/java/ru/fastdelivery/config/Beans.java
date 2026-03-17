package ru.fastdelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.currency.CurrencyPropertiesProvider;
import ru.fastdelivery.domain.common.geo.GeoPropertiesProvider;
import ru.fastdelivery.domain.common.geo.HaversineDistanceProvider;
import ru.fastdelivery.usecase.GeoDistanceProvider;
import ru.fastdelivery.usecase.TariffCalculateUseCase;
import ru.fastdelivery.usecase.VolumePriceProvider;
import ru.fastdelivery.usecase.WeightPriceProvider;

/**
 * Определение реализаций бинов для всех модулей приложения
 */
@Configuration
public class Beans {

    @Bean
    public CurrencyFactory currencyFactory(CurrencyPropertiesProvider currencyProperties) {
        return new CurrencyFactory(currencyProperties);
    }

    @Bean
    public TariffCalculateUseCase tariffCalculateUseCase(
            WeightPriceProvider weightPriceProvider,
            VolumePriceProvider volumePriceProvider,
            GeoDistanceProvider geoDistanceProvider,
            GeoPropertiesProvider geoPropertiesProvider) {

        return new TariffCalculateUseCase(
                weightPriceProvider,
                volumePriceProvider,
                geoDistanceProvider,
                geoPropertiesProvider);
    }

    @Bean
    public GeoDistanceProvider geoDistanceProvider() {
        return new HaversineDistanceProvider();
    }
}
