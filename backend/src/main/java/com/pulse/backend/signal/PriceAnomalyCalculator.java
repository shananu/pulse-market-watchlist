package com.pulse.backend.signal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class PriceAnomalyCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    public PriceAnomalyResult calculate(
            List<BigDecimal> historicalPrices,
            BigDecimal currentPrice
    ) {

        if (historicalPrices == null || historicalPrices.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two historical prices are required"
            );
        }

        BigDecimal previousPrice =
                historicalPrices.get(historicalPrices.size() - 1);

        BigDecimal currentReturn =
                currentPrice
                        .subtract(previousPrice)
                        .divide(previousPrice, MC);

        List<BigDecimal> returns = new java.util.ArrayList<>();

        for (int i = 1; i < historicalPrices.size(); i++) {

            BigDecimal previous = historicalPrices.get(i - 1);
            BigDecimal current = historicalPrices.get(i);

            BigDecimal dailyReturn =
                    current
                            .subtract(previous)
                            .divide(previous, MC);

            returns.add(dailyReturn);
        }

        BigDecimal mean = returns.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(returns.size()),
                        MC
                );

        BigDecimal variance = returns.stream()
                .map(r -> r.subtract(mean).pow(2, MC))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(returns.size()),
                        MC
                );

        BigDecimal volatility = BigDecimal.valueOf(
                Math.sqrt(variance.doubleValue())
        );

        BigDecimal zScore;

        if (volatility.compareTo(BigDecimal.ZERO) == 0) {
            zScore = BigDecimal.ZERO;
        } else {
            zScore = currentReturn
                    .abs()
                    .divide(volatility, MC);
        }

        BigDecimal score = scoreFromZ(zScore);

        return new PriceAnomalyResult(
                currentReturn,
                volatility,
                zScore,
                score
        );
    }

    private BigDecimal scoreFromZ(BigDecimal zScore) {

        if (zScore.compareTo(BigDecimal.ONE) < 0) {
            return new BigDecimal("25");
        }

        if (zScore.compareTo(new BigDecimal("2")) < 0) {
            return new BigDecimal("50");
        }

        if (zScore.compareTo(new BigDecimal("3")) < 0) {
            return new BigDecimal("75");
        }

        return new BigDecimal("100");
    }
}