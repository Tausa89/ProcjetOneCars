package com.app.domain.car;

import com.app.domain.car.extensions.CarsJsonFileExtension;
import com.app.domain.car.type.Color;
import com.app.domain.car.type.SortingType;
import com.app.service.CarsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(CarsJsonFileExtension.class)
@RequiredArgsConstructor
class CarsServiceTest {

    private final CarsService carsService;

    //TODO wstrzykiwanie z pliku


    @Test
    @DisplayName("when select cars with higher mileage than given")
    void testOne() {

        double mileage = 10000;


        var sorted = carsService.findAllWithMileageGreaterThan(mileage);

        var expectedCar = Car
                .builder()
                .model("Audi")
                .price(BigDecimal.valueOf(100000))
                .color(Color.BLACK)
                .mileage(25000)
                .build();

        assertThat(sorted).hasSize(1);
        assertThat(sorted.get(0)).usingRecursiveComparison().isEqualTo(expectedCar);



    }


    @Test
    @DisplayName("when group cars correctly by their colors")
    void testTwo(){

        var sorted = carsService.returnAmountOfCarsForAllColors();

        assertThat(sorted).hasSize(2);
        assertThat(sorted.get(Color.BLACK)).isEqualTo(2);
        assertThat(sorted.get(Color.WHITE)).isEqualTo(1);
    }

    @Test
    @DisplayName("when select most expensive car for every model")
    void testThree(){

        var sorted = carsService.returnMostExpensiveCarForEveryModel();

        assertThat(sorted).hasSize(2);
        assertThat(sorted.get("Audi").price).isEqualTo(BigDecimal.valueOf(100000));
        assertThat(sorted.get("BMW").price).isEqualTo(BigDecimal.valueOf(5555500));

    }


//    @Test
//    @DisplayName("when statistic are correct")
//    void testFour(){
//
//        var statistic = carsService.getStatisticPriceAndMileage();
//
//        assertThat(statistic).isNotBlank();
//        assertThat(statistic).isNotNull();
//
//    }

    @Test
    @DisplayName("when correctly select the most expensive car")
    void testFive(){

        var expectedCar = Car
                .builder()
                .model("BMW")
                .price(BigDecimal.valueOf(5555500))
                .color(Color.BLACK)
                .mileage(0)
                .components(List.of("Mirrors","ABS","GPS"))
                .build();

        var car = carsService.getTheMostExpensiveCar();


        assertThat(car).usingRecursiveComparison().isEqualTo(expectedCar);
    }


    static List<String> componentsList(){
        return List.of("ABS","Windows","XYZ");
    }


    @ParameterizedTest
    @MethodSource("componentsList")
    @DisplayName("when components are sorted alphabetical")
    void testSix(String componentsList){


        System.out.println(componentsList);
        System.out.println("****");


        var sorted = carsService.sortAlphabeticalComponentList();
        sorted.forEach(p -> System.out.println(p.components));

        assertThat(sorted.get(0).components.get(0)).isEqualTo(componentsList);
        assertThat(sorted.get(0).components.get(1)).isEqualTo(componentsList);
        assertThat(sorted.get(2).components).isEmpty();
        assertThat(sorted.get(1).components.get(1)).isEqualTo("GPS");
    }


    @Test
    @DisplayName("when select correctly car in given price range")
    void testSeven(){

        var minPrice = BigDecimal.valueOf(10000);
        var maxPrice = BigDecimal.valueOf(300000);
        var sorted = carsService.getCarsWithGivenPriceRange(minPrice,maxPrice);


        assertThat(sorted).hasSize(2);
        assertThat(sorted.get(0).model).isEqualTo("Audi");
    }

    @Test
    @DisplayName("when min price is bigger than max price")
    void testEight(){


        assertThrows(IllegalStateException.class, () -> {
            var minPrice = BigDecimal.valueOf(10000);
            var maxPrice = BigDecimal.valueOf(1000);
            var sorted = carsService.getCarsWithGivenPriceRange(minPrice,maxPrice);
        });


    }


    @Test
    @DisplayName("when price or max price is null")
    void testNine(){

        assertThrows(IllegalStateException.class, () -> {

            var maxPrice = BigDecimal.valueOf(1000);
            var sorted = carsService.getCarsWithGivenPriceRange(null,maxPrice);
        });

        assertThrows(IllegalStateException.class, () -> {
            var minPrice = BigDecimal.valueOf(10000);
            var sorted = carsService.getCarsWithGivenPriceRange(minPrice,null);
        });

    }

    @Test
    @DisplayName("when cars are grouped correctly by components they have")
    void testTen(){

        var sorted = carsService.groupByComponent();

        assertThat(sorted).hasSize(5);
        assertThat(sorted.get("ABS").size()).isEqualTo(2);
        assertThat(sorted.get("XYZ").size()).isEqualTo(1);
    }






}