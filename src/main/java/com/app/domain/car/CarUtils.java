package com.app.domain.car;

import com.app.domain.car.type.Color;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface CarUtils {
    Comparator<Car> compareByModel = Comparator.comparing(car -> car.model);
    Comparator<Car> compareByPrice = Comparator.comparing(car -> car.price);
    Comparator<Car> compareByMileage = Comparator.comparing(car -> car.mileage);
    Comparator<Car> compareByColor = Comparator.comparing(car -> car.color);


    Function<Car, Double> toMileage = car -> car.mileage;
    Function<Car, String> toModel = car -> car.model;
    Function<Car, Color> toColor = car -> car.color;
    Function<Car, BigDecimal> toPrice = car -> car.price;
    Function<Car, List<String>> toComponents = car -> new ArrayList<>(car.components);


}
