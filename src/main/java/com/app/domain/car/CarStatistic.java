package com.app.domain.car;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarStatistic {

    private Statistic<BigDecimal> price;
    private Statistic<Double> mileage;

}
