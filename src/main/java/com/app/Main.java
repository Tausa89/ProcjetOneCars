package com.app;

import com.app.domain.car.Car;
import com.app.domain.car.CarStatistic;
import com.app.domain.car.type.StatisticAttribute;
import com.app.service.CarsService;
import com.app.domain.car.type.Color;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {



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

        var list = List.of(carOne,carTwo,carThree);
        CarsService carStatistic = new CarsService(list);

        var stats = carStatistic.getStatisticForGivenAttribute(StatisticAttribute.PRICE);
        var max = stats.getMileage().getMax();

        System.out.println(max);

    }
}
