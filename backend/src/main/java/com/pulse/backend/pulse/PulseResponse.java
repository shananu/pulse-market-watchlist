package com.pulse.backend.pulse;

import com.pulse.backend.attention.AttentionEventResponse;
import java.time.OffsetDateTime;
import java.util.List;

public record PulseResponse(
    OffsetDateTime lastCheckedAt,
    long hoursSinceLastCheck,
    int meaningfulChangeCount,
    List<AttentionEventResponse> attentionItems,
    int unremarkableCount
) {}