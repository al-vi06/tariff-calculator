package ru.fastdelivery.calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.fastdelivery.ControllerTest;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.request.GeoPointRequest;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CalculateControllerTest extends ControllerTest {
    final String baseCalculateApi = "/api/v1/calculate/";
    @MockBean TariffCalculateUseCase useCase;
    @MockBean CurrencyFactory currencyFactory;

    final String RUB = "RUB";
    final Price rubPrice(double v) {
        return new Price(BigDecimal.valueOf(v), new CurrencyFactory(c -> true).create(RUB));
    }

    @Test
    @DisplayName("Валидные данные без габаритов и координат -> 200")
    void whenValidWeightOnly_thenReturn200() {
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(BigInteger.TEN, null, null, null)),
                RUB, null, null);

        when(useCase.calc(any())).thenReturn(rubPrice(10));
        when(useCase.minimalPrice()).thenReturn(rubPrice(5));
        doNothing().when(useCase).validateCoordinates(any());

        var response = restTemplate.postForEntity(baseCalculateApi, request, CalculatePackagesResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Валидные данные с габаритами -> 200")
    void whenValidWithDimensions_thenReturn200() {
        var pkg = new CargoPackage(
                BigInteger.valueOf(4564),
                BigInteger.valueOf(345),
                BigInteger.valueOf(589),
                BigInteger.valueOf(234));
        var request = new CalculatePackagesRequest(List.of(pkg), RUB, null, null);

        when(useCase.calc(any())).thenReturn(rubPrice(200));
        when(useCase.minimalPrice()).thenReturn(rubPrice(5));

        var response = restTemplate.postForEntity(baseCalculateApi, request, CalculatePackagesResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Валидные данные с координатами -> 200")
    void whenValidWithCoordinates_thenReturn200() {
        var pkg = new CargoPackage(BigInteger.TEN, null, null, null);
        var dep = new GeoPointRequest(BigDecimal.valueOf(55.4), BigDecimal.valueOf(65.3));
        var dst = new GeoPointRequest(BigDecimal.valueOf(73.3), BigDecimal.valueOf(55.0));
        var request = new CalculatePackagesRequest(List.of(pkg), RUB, dep, dst);

        when(useCase.calc(any())).thenReturn(rubPrice(500));
        when(useCase.minimalPrice()).thenReturn(rubPrice(5));
        doNothing().when(useCase).validateCoordinates(any());

        var response = restTemplate.postForEntity(baseCalculateApi, request, CalculatePackagesResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Список упаковок == null -> 400")
    void whenNullPackages_thenReturn400() {
        var request = new CalculatePackagesRequest(null, RUB, null, null);
        var response = restTemplate.postForEntity(baseCalculateApi, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Код валюты == null -> 400")
    void whenNullCurrency_thenReturn400() {
        var request = new CalculatePackagesRequest(
                List.of(new CargoPackage(BigInteger.TEN, null, null, null)),
                null, null, null);
        var response = restTemplate.postForEntity(baseCalculateApi, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
