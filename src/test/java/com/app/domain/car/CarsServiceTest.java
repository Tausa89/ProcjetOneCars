package com.app.domain.car;

import com.app.domain.car.extensions.CarsJsonFileExtension;
import com.app.domain.car.type.Color;
import com.app.domain.car.type.StatisticAttribute;
import com.app.service.CarsService;
import com.app.service.exception.CarsServiceException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
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


    @Test
    @DisplayName("when mileage statistic are correct")
    void testFour(){


        var statistic = carsService.getStatisticForGivenAttribute(StatisticAttribute.MILEAGE);

        assertThat(statistic.getMileage().getMin()).isEqualTo(0);
        assertThat(statistic.getMileage().getMax()).isEqualTo(25000);
        assertThat(statistic).isNotNull();


    }

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





    @Test
    @DisplayName("when components are sorted alphabetical")
    void testSix(){


        var sorted = carsService.sortAlphabeticalComponentList();

        assertThat(sorted.get(0).components.get(0)).isEqualTo("ABS");
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


        var exception = assertThrows(CarsServiceException.class, () -> {
            var minPrice = BigDecimal.valueOf(10000);
            var maxPrice = BigDecimal.valueOf(1000);
            carsService.getCarsWithGivenPriceRange(minPrice,maxPrice);
        });
        assertEquals("Minimal price can't be lowe than maximal price",exception.getMessage());


    }


    @Test
    @DisplayName("when price or max price is null")
    void testNine(){

        assertThrows(CarsServiceException.class, () -> {

            var maxPrice = BigDecimal.valueOf(1000);
            carsService.getCarsWithGivenPriceRange(null,maxPrice);
        });

        assertThrows(CarsServiceException.class, () -> {
            var minPrice = BigDecimal.valueOf(10000);
            carsService.getCarsWithGivenPriceRange(minPrice,null);
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

    @Test
    @DisplayName("When statistic attribute is null")
    void testEleven(){

        assertThrows(CarsServiceException.class, () -> carsService.getStatisticForGivenAttribute(null));

    }

    @Test
    @DisplayName("when price statistic are correct")
    void testTwelve(){


        var statistic = carsService.getStatisticForGivenAttribute(StatisticAttribute.PRICE);

        assertThat(statistic.getPrice().getMin()).isEqualByComparingTo(BigDecimal.valueOf(25000));
        assertThat(statistic.getPrice().getMax()).isEqualByComparingTo(BigDecimal.valueOf(5555500));
        assertThat(statistic).isNotNull();
        assertThat(statistic.getPrice().getAverage()).isCloseTo(BigDecimal.valueOf(1893000), withinPercentage(1));


    }






}