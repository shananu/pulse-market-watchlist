package com.pulse.backend.market;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketSnapshotService marketSnapshotService;

    public MarketController(MarketSnapshotService marketSnapshotService) {
        this.marketSnapshotService = marketSnapshotService;
    }

    @PostMapping("/{symbol}/capture")
    public SnapshotResponse capture(
            @PathVariable String symbol
    ) {
        return SnapshotResponse.from(
                marketSnapshotService.captureLatest(symbol)
        );
    }

    @GetMapping("/{symbol}/history")
    public List<SnapshotResponse> history(
            @PathVariable String symbol
    ) {
        return marketSnapshotService.getHistory(symbol)
                .stream()
                .map(SnapshotResponse::from)
                .toList();
    }

    public record SnapshotResponse(
            String symbol,
            BigDecimal price,
            Long volume,
            OffsetDateTime timestamp,
            String source
    ) {
        static SnapshotResponse from(MarketSnapshot snapshot) {
            return new SnapshotResponse(
                    snapshot.getStock().getSymbol(),
                    snapshot.getPrice(),
                    snapshot.getVolume(),
                    snapshot.getTimestamp(),
                    snapshot.getSource()
            );
        }
    }
}