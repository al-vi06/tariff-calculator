package ru.fastdelivery.presentation.calc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.geo.GeoPoint;
import ru.fastdelivery.domain.common.length.Length;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.OuterDimensions;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.request.GeoPointRequest;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calculate/")
@RequiredArgsConstructor
@Tag(name = "Расчеты стоимости доставки")
public class CalculateController {
    private final TariffCalculateUseCase tariffCalculateUseCase;
    private final CurrencyFactory currencyFactory;

    @PostMapping
    @Operation(summary = "Расчет стоимости по упаковкам груза")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public CalculatePackagesResponse calculate(
            @Valid @RequestBody CalculatePackagesRequest request) {

//        var packsWeights = request.packages().stream()
//                .map(CargoPackage::weight)
//                .map(Weight::new)
//                .map(Pack::new)
//                .toList();
//        var shipment = new Shipment(packsWeights, currencyFactory.create(request.currencyCode()));
//        var calculatedPrice = tariffCalculateUseCase.calc(shipment);
//        var minimalPrice = tariffCalculateUseCase.minimalPrice();
//        return new CalculatePackagesResponse(calculatedPrice, minimalPrice);

        GeoPoint departure = toGeoPoint(request.departure());
        GeoPoint destination = toGeoPoint(request.destination());

        if (departure != null) tariffCalculateUseCase.validateCoordinates(departure);
        if (destination != null) tariffCalculateUseCase.validateCoordinates(destination);

        List<Pack> packs = request.packages().stream()
                .map(this::toPack)
                .toList();

        var currency = currencyFactory.create(request.currencyCode());
        var shipment = new Shipment(packs, currency, departure, destination);

        var calculatedPrice = tariffCalculateUseCase.calc(shipment);
        var minimalPrice = tariffCalculateUseCase.minimalPrice();

        return new CalculatePackagesResponse(calculatedPrice, minimalPrice);
    }

    private Pack toPack(CargoPackage pkg) {
        var weight = new Weight(pkg.weight());
        if (pkg.length() != null && pkg.width() != null && pkg.height() != null) {
            var dims = new OuterDimensions(
                    new Length(pkg.length()),
                    new Length(pkg.width()),
                    new Length(pkg.height())
            );
            return new Pack(weight, dims);
        }
        return new Pack(weight);
    }

    private GeoPoint toGeoPoint(GeoPointRequest req) {
        if (req == null) return null;
        return new GeoPoint(req.latitude(), req.longitude());
    }
}

