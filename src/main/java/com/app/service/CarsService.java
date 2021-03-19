package com.app.service;

import com.app.domain.car.Car;
import com.app.domain.car.CarStatistic;
import com.app.domain.car.CarUtils;
import com.app.domain.car.Statistic;
import com.app.domain.car.type.SortingType;

import com.app.domain.car.type.Color;
import com.app.domain.car.type.StatisticAttribute;
import com.app.service.exception.CarsServiceException;
import org.eclipse.collections.impl.collector.BigIntegerSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CarsService {

    private List<Car> cars;

    public CarsService(List<Car> cars) {
        this.cars = cars;
    }



    @Override
    public String toString() {
        return "Cars{" +
                "cars=" + cars +
                '}';
    }


    /**
     *
     * @param sortingType
     * @param descending
     * @return
     */
    public List<Car> sortingByGivenOrder(SortingType sortingType, boolean descending) {

        if (sortingType == null) {
            throw new IllegalStateException("Sorting Type is null");
        }

        var sortedCars = switch (sortingType) {
            case MODEL -> cars.stream().sorted(CarUtils.compareByModel);
            case COLOR -> cars.stream().sorted(CarUtils.compareByColor);
            case PRICE -> cars.stream().sorted(CarUtils.compareByPrice);
            default -> cars.stream().sorted(CarUtils.compareByMileage);
        };

        var sortedCarsCollection = sortedCars.collect(Collectors.toList());

        if (descending) {
            Collections.reverse(sortedCarsCollection);
        }

        return sortedCarsCollection;
    }

    /**
     *
     * @param mileage
     * @return
     */
    public List<Car> findAllWithMileageGreaterThan(double mileage) {

        if (mileage <= 0){
            throw new IllegalStateException("Mileage must have positive value");
        }

        return cars.stream()
                .filter(car -> car.hasMileageGreaterThan(mileage))
                .collect(Collectors.toList());
    }

    /**
     *
     * @return
     */
    public Map<Color, Long> returnAmountOfCarsForAllColors() {
        return cars.stream()
                .collect(Collectors.groupingByConcurrent(CarUtils.toColor, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::max, LinkedHashMap::new));
    }


    public Map<String, Car> returnMostExpensiveCarForEveryModel() {
        return cars.stream()
                .collect(Collectors.groupingBy(CarUtils.toModel, Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(CarUtils.toPrice)),
                        highestPriceCar -> highestPriceCar.orElseThrow(() -> new CarsServiceException("Cannot find the most expensive car")))));
    }




    public CarStatistic getStatisticForGivenAttribute(StatisticAttribute statisticAttribute){

        if (Objects.isNull(statisticAttribute)){

            throw new CarsServiceException("Attribute can't be null");
        }

        return switch (statisticAttribute){

            case PRICE -> getPriceStatistic();
            case MILEAGE -> getMileageStatistic();

        };
    }

    private CarStatistic getMileageStatistic() {

        var mileageStatistic = cars.stream().collect(Collectors.summarizingDouble(CarUtils.toMileage::apply));

        return CarStatistic
                .builder()
                .mileage(Statistic
                        .<Double>
                        builder()
                        .min(mileageStatistic.getMin())
                        .max(mileageStatistic.getMax())
                        .average(mileageStatistic.getAverage())
                        .build())
                .build();
    }

    private CarStatistic getPriceStatistic() {

        var priceStatistic = cars.stream().collect(Collectors2.summarizingBigDecimal(CarUtils.toPrice::apply));

        return CarStatistic
                .builder()
                .price(Statistic
                        .<BigDecimal>builder()
                        .min(priceStatistic.getMin())
                        .max(priceStatistic.getMax())
                        .average(priceStatistic.getAverage())
                        .build())
                .build();

    }


//    public String getStatisticPriceAndMileage() {
//
//        long count = cars.size();
//
//        BigDecimal sum = cars.stream().map(Car::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal maxPrice = cars.stream().max(Comparator.comparing(Car::getPrice)).orElseThrow(() -> new IllegalStateException("Error")).getPrice();
//
//        BigDecimal minPrice = cars.stream().min(Comparator.comparing(Car::getPrice)).orElseThrow(() -> new IllegalStateException("Error")).getPrice();
//
//        BigDecimal averagePrice = sum.divide(new BigDecimal(count), 2, RoundingMode.DOWN);
//
//        DoubleSummaryStatistics mileageStats = cars.stream().collect(Collectors.summarizingDouble(Car::getMileage));
//
//        return "Highest price is " + maxPrice + ".\n"
//                + "Lowest price is " + minPrice + ".\n"
//                + "Average price for all cars is " + averagePrice + ".\n"
//                + "Highest mileage is " + mileageStats.getMax() + ".\n"
//                + "Lowest mileage is " + mileageStats.getMin() + ".\n"
//                + "Average mileage for all cars is " + mileageStats.getAverage() + ".";
//
//    }

    public Car getTheMostExpensiveCar() {

        return cars
                .stream()
                .collect(Collectors.groupingBy(CarUtils.toPrice))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new IllegalStateException("..."))
                .getValue().get(0);
    }

    public  List<Car> sortAlphabeticalComponentList() {
        return cars
                .stream()
                .map(Car::withSortedComponents)
                .collect(Collectors.toList());
    }



    public List<Car> getCarsWithGivenPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {

        if (Objects.isNull(minPrice)) {
            throw new IllegalStateException("....");
        }

        if (Objects.isNull(maxPrice)) {
            throw new IllegalStateException("...");
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalStateException("...");
        }

//        return cars.stream()
//                .filter(car -> car.getPrice().compareTo(minPrice) > 0 && car.getPrice().compareTo(maxPrice) < 0)
//                .sorted(Comparator.comparing(CarUtils.toModel))
//                .collect(Collectors.toList());

        return cars.stream()
                .filter(car -> car.hasPriceGreaterThan(minPrice)  && !car.hasPriceGreaterThan(maxPrice))
                .sorted(Comparator.comparing(CarUtils.toModel))
                .collect(Collectors.toList());

    }

    public Map<String, List<Car>> groupByComponent() {
        return cars
                .stream()
                .flatMap(CarUtils.toComponents.andThen(Collection::stream))
                .distinct()
                .collect(Collectors.toMap(
                        component -> component,
                        component -> cars
                                .stream()
                                .filter(car -> car.doesContainComponent(component))
                                .collect(Collectors.toList())
                ));
    }

//    public Map<String, List<Car>> groupByComponent() {
//        return cars
//                .stream()
//                .flatMap(car -> car.getComponents().stream())
//                .distinct()
//                .collect(Collectors.toMap(
//                        component -> component,
//                        component -> cars
//                                .stream()
//                                .filter(car -> car.doseComponentsContain(component))
//                                .collect(Collectors.toList())
//                ));
//    }





}
