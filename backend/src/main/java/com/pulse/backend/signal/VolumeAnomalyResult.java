package com.pulse.backend.signal;

import java.math.BigDecimal;

public record VolumeAnomalyResult(
        BigDecimal currentVolume,
        BigDecimal historicalAverageVolume,
        BigDecimal volumeRatio,
        BigDecimal score
) {
}