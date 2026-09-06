package com.pulse.backend.market.provider;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MarketDataPoint(
        String symbol,
        BigDecimal price,
        Long volume,
        OffsetDateTime timestamp
) {
}