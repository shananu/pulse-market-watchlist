package com.pulse.backend.significance;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
//import java.util.ArrayList;
import java.util.List;

@Component
public class SignificanceCalculator {

    private static final BigDecimal PRICE_WEIGHT =
            new BigDecimal("0.30");

    private static final BigDecimal RELATIVE_WEIGHT =
            new BigDecimal("0.25");

    private static final BigDecimal EVENT_WEIGHT =
            new BigDecimal("0.20");

    private static final BigDecimal VOLUME_WEIGHT =
            new BigDecimal("0.15");

    private static final BigDecimal PERSISTENCE_WEIGHT =
            new BigDecimal("0.10");

    public SignificanceResult calculate(
            BigDecimal priceScore,
            BigDecimal relativeScore,
            BigDecimal eventScore,
            BigDecimal volumeScore,
            BigDecimal persistenceScore
    ) {
        List<BigDecimal> scores = List.of(
                safe(priceScore),
                safe(relativeScore),
                safe(eventScore),
                safe(volumeScore),
                safe(persistenceScore)
        );

        BigDecimal weightedScore =
                safe(priceScore).multiply(PRICE_WEIGHT)
                        .add(safe(relativeScore).multiply(RELATIVE_WEIGHT))
                        .add(safe(eventScore).multiply(EVENT_WEIGHT))
                        .add(safe(volumeScore).multiply(VOLUME_WEIGHT))
                        .add(safe(persistenceScore).multiply(PERSISTENCE_WEIGHT));

        int strongSignalCount = 0;

        for (BigDecimal score : scores) {
            if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
                strongSignalCount++;
            }
        }

        BigDecimal bonus = BigDecimal.ZERO;

        if (strongSignalCount >= 4) {
            bonus = BigDecimal.valueOf(15);
        } else if (strongSignalCount >= 3) {
            bonus = BigDecimal.valueOf(10);
        }

        BigDecimal finalScore = weightedScore
                .add(bonus)
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        return new SignificanceResult(
                finalScore,
                getBand(finalScore),
                strongSignalCount,
                bonus.compareTo(BigDecimal.ZERO) > 0
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String getBand(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(30)) < 0) {
            return "NORMAL";
        }

        if (score.compareTo(BigDecimal.valueOf(50)) < 0) {
            return "WATCH";
        }

        if (score.compareTo(BigDecimal.valueOf(70)) < 0) {
            return "NOTABLE";
        }

        return "SIGNIFICANT";
    }
}