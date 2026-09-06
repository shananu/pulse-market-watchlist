package com.pulse.backend.signal;

import java.math.BigDecimal;

public record RelativePerformanceResult(
        BigDecimal stockReturn,
        BigDecimal benchmarkReturn,
        BigDecimal divergence,
        BigDecimal score
) {
}