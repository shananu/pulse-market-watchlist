package com.pulse.backend.signal;

import com.pulse.backend.market.MarketSnapshot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class VolumeAnomalyCalculator {

    public VolumeAnomalyResult calculate(
            List<MarketSnapshot> historicalSnapshots,
            Long currentVolume
    ) {
        if (historicalSnapshots == null || historicalSnapshots.isEmpty()) {
            throw new IllegalArgumentException(
                    "Not enough historical data for volume anomaly"
            );
        }

        if (currentVolume == null || currentVolume <= 0) {
            throw new IllegalArgumentException(
                    "Current volume must be positive"
            );
        }

        double averageVolume =
                historicalSnapshots.stream()
                        .filter(s -> s.getVolume() != null)
                        .mapToLong(MarketSnapshot::getVolume)
                        .average()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Historical volume unavailable"
                                ));

        BigDecimal average = BigDecimal.valueOf(averageVolume);

        BigDecimal ratio = BigDecimal.valueOf(currentVolume)
                .divide(average, 4, RoundingMode.HALF_UP);

        BigDecimal score;

        double r = ratio.doubleValue();

        if (r < 1.25) {
            score = BigDecimal.valueOf(25);
        } else if (r < 1.5) {
            score = BigDecimal.valueOf(50);
        } else if (r < 2.0) {
            score = BigDecimal.valueOf(75);
        } else {
            score = BigDecimal.valueOf(100);
        }

        return new VolumeAnomalyResult(
                BigDecimal.valueOf(currentVolume),
                average.setScale(2, RoundingMode.HALF_UP),
                ratio,
                score
        );
    }
}