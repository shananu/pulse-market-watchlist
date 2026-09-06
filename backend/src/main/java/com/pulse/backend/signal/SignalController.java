package com.pulse.backend.signal;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/signals")
public class SignalController {

    private final SignalService signalService;

    public SignalController(SignalService signalService) {
        this.signalService = signalService;
    }

    @GetMapping("/{symbol}/price-anomaly")
    public PriceAnomalyResult priceAnomaly(
            @PathVariable String symbol,
            @RequestParam BigDecimal currentPrice) {
        return signalService.calculatePriceAnomaly(
                symbol,
                currentPrice);
    }

    @GetMapping("/{symbol}/volume-anomaly")
    public VolumeAnomalyResult volumeAnomaly(
            @PathVariable String symbol,
            @RequestParam Long currentVolume) {
        return signalService.calculateVolumeAnomaly(
                symbol,
                currentVolume);
    }

    @GetMapping("/{symbol}/relative-performance")
    public RelativePerformanceResult relativePerformance(
            @PathVariable String symbol,
            @RequestParam BigDecimal currentPrice,
            @RequestParam BigDecimal benchmarkReturn) {
        return signalService.calculateRelativePerformance(
                symbol,
                currentPrice,
                benchmarkReturn);
    }

    @GetMapping("/{symbol}/signals")
    public java.util.List<SignalResponse> signals(@PathVariable String symbol) {
        return signalService.getSignals(symbol).stream()
                .map(s -> new SignalResponse(
                        s.getSignalType().name(),
                        s.getValue(),
                        s.getScore(),
                        s.getMetadata(),
                        s.getTimestamp()))
                .toList();
    }
}