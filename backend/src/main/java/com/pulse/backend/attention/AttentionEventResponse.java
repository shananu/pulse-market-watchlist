package com.pulse.backend.attention;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AttentionEventResponse(
        Long id,
        String symbol,
        BigDecimal attentionScore,
        String status,
        OffsetDateTime firstDetectedAt,
        OffsetDateTime surfacedAt,
        OffsetDateTime viewedAt,
        OffsetDateTime reviewedAt,
        OffsetDateTime createdAt
) {
}