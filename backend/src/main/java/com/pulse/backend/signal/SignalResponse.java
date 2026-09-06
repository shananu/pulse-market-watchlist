package com.pulse.backend.signal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SignalResponse(
        String signalType,
        BigDecimal value,
        BigDecimal score,
        String metadata,
        OffsetDateTime timestamp) {}