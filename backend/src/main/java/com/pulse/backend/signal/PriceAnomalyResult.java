package com.pulse.backend.signal;

import java.math.BigDecimal;

public record PriceAnomalyResult(
        BigDecimal currentReturn,
        BigDecimal historicalVolatility,
        BigDecimal zScore,
        BigDecimal score
) {
}