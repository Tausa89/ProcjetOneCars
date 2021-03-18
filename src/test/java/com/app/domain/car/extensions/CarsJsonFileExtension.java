package com.app.domain.car.extensions;

import com.app.domain.car.Car;
import com.app.domain.car.type.Color;
import com.app.service.CarsService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.math.BigDecimal;
import java.util.List;

public class CarsJsonFileExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CarsService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Car carOne = Car
                .builder()
                .model("Audi")
                .price(BigDecimal.valueOf(25000))
                .color(Color.WHITE)
                .mileage(500)
                .components(List.of("Windows","ABS","XYZ"))
                .build();
        Car carTwo = Car
                .builder()
                .model("BMW")
                .price(BigDecimal.valueOf(5555500))
                .color(Color.BLACK)
                .mileage(0)
                .components(List.of("Mirrors","ABS","GPS"))
                .build();

        Car carThree = Car
                .builder()
                .model("Audi")
                .price(BigDecimal.valueOf(100000))
                .color(Color.BLACK)
                .mileage(25000)
                .build();

        return new CarsService(List.of(carOne, carTwo, carThree));
    }
}
