package com.pulse.backend.market.provider;

import java.math.BigDecimal;

public record DemoScenario(
        String symbol,
        BigDecimal price,
        Long volume,
        int minute
) {
}