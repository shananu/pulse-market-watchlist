package com.pulse.backend.signal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RelativePerformanceCalculator {

    public RelativePerformanceResult calculate(
            BigDecimal currentPrice,
            BigDecimal previousPrice,
            BigDecimal benchmarkReturn
    ) {
        if (previousPrice == null || previousPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Previous price must be positive");
        }

        BigDecimal stockReturn = currentPrice
                .subtract(previousPrice)
                .divide(previousPrice, 8, RoundingMode.HALF_UP);

        BigDecimal divergence = stockReturn.subtract(benchmarkReturn);

        double d = Math.abs(divergence.doubleValue());

        BigDecimal score;

        if (d < 0.005) {
            score = BigDecimal.valueOf(25);
        } else if (d < 0.01) {
            score = BigDecimal.valueOf(50);
        } else if (d < 0.02) {
            score = BigDecimal.valueOf(75);
        } else {
            score = BigDecimal.valueOf(100);
        }

        return new RelativePerformanceResult(
                stockReturn,
                benchmarkReturn,
                divergence,
                score
        );
    }
}