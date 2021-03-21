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
     * Method provide ability to sort collection of Cars by chosen sortingType give as
     * parameter and possibility to chose descending or ascending sorting.
     * @param sortingType Enum decide according to what collection should be sorted
     * @param descending boolean decide about ascending or descending order or collection
     * @return sorted List by given parameters.
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
     * Method provide ability to select from given collection of Cars all cars with
     * higher mileage than give as parameter
     * @param mileage double value above which cars would be selected
     * @return List of cars with higher mileage than given as parameter or CarServiceException
     * when mileage parameter is negative value.
     */
    public List<Car> findAllWithMileageGreaterThan(double mileage) {

        if (mileage <= 0){
            throw new CarsServiceException("Mileage must have positive value");
        }

        return cars.stream()
                .filter(car -> car.hasMileageGreaterThan(mileage))
                .collect(Collectors.toList());
    }

    /**
     *Method provide ability to grouped collection of Cars by their colors and count how many of them is for
     * every color.
     * @return collection grouped by Cars colors with amount of cars for every color.
     */
    public Map<Color, Long> returnAmountOfCarsForAllColors() {
        return cars.stream()
                .collect(Collectors.groupingByConcurrent(CarUtils.toColor, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::max, LinkedHashMap::new));
    }


    /**
     * Method provide ability to select the mose expensive car for every car model.
     * @return collection of cars where for every model have selected the mose expensive car or CarServiceException
     * if there is no possibility of finding the most expensive car.
     */
    public Map<String, Car> returnMostExpensiveCarForEveryModel() {
        return cars.stream()
                .collect(Collectors.groupingBy(CarUtils.toModel, Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(CarUtils.toPrice)),
                        highestPriceCar -> highestPriceCar.orElseThrow(() -> new CarsServiceException("Cannot find the most expensive car")))));
    }


    /**
     * Method provide base statistic like minimal, maximal or average value for whole collection taking as a argument
     * parameter for which on statistic should be counted.
     * @param statisticAttribute Enum decide which statistic should be counted.
     * @return cars statistics for given as parameter required attribute.
     */
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


    /**
     * Method select the most expensive car from whole collection
     * @return the most expensive car in collection of cars or CarsServiceException if there is any.
     */
    public Car getTheMostExpensiveCar() {

        return cars
                .stream()
                .collect(Collectors.groupingBy(CarUtils.toPrice))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new CarsServiceException("Can't find the most expensive car"))
                .getValue().get(0);
    }

    /**
     * method sort alphabetically list of components for every car.
     * @return sorted alphabetically component list.
     */
    public  List<Car> sortAlphabeticalComponentList() {
        return cars
                .stream()
                .map(Car::withSortedComponents)
                .collect(Collectors.toList());
    }


    /**
     * Method select from whole collection cars in given price range.
     * @param minPrice BigDecimal the lowest price range for searching.
     * @param maxPrice BigDecimal the highest price range for searching
     * @return collection of cars with price higher than minimal price given as parameter and lower price than
     * maximal price given as parameter. Null if any of price is not filled or CarServiceException when minPrice is
     * higher than maxPrice
     */
    public List<Car> getCarsWithGivenPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {

        if (Objects.isNull(minPrice)) {
            throw new CarsServiceException("Price can't be null");
        }

        if (Objects.isNull(maxPrice)) {
            throw new CarsServiceException("Price can't be null");
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new CarsServiceException("Minimal price can't be lowe than maximal price");
        }


        return cars.stream()
                .filter(car -> car.hasPriceGreaterThan(minPrice)  && !car.hasPriceGreaterThan(maxPrice))
                .sorted(Comparator.comparing(CarUtils.toModel))
                .collect(Collectors.toList());

    }

    /**
     * Method grouped cars by components their have.
     * @return collections of cars for every component specified car have.
     */

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







}
