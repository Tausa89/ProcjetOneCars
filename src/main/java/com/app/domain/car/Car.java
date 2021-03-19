package com.app.domain.car;


import com.app.domain.car.type.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Car {
    String model;
    BigDecimal price;
    Color color;
    double mileage;

    @Builder.Default
    List<String> components = new ArrayList<>();

    public boolean hasMileageGreaterThan(double mileage) {
        return this.mileage > mileage;
    }

    public boolean hasPriceGreaterThan(BigDecimal price){
        return this.price.compareTo(price) > 0;
    }

    public Car withSortedComponents(){
        return Car
                .builder()
                .model(model)
                .price(price)
                .color(color)
                .mileage(mileage)
                .components(components.stream().sorted().collect(Collectors.toList()))
                .build();
    }

    public boolean doesContainComponent(String component){
        return this.components.contains(component);
    }

    @Override
    public String toString() {
        return "Car{" +
                "model='" + model + '\'' +
                ", price=" + price +
                ", color=" + color +
                ", mileage=" + mileage +
                ", components=" + components +
                '}';
    }
}
