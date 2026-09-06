package com.pulse.backend.significance;

import java.math.BigDecimal;

public record SignificanceResult(
        BigDecimal score,
        String band,
        int strongSignalCount,
        boolean convergenceBonus
) {
}