package com.pulse.backend.signal;

import com.pulse.backend.market.MarketSnapshot;
import com.pulse.backend.market.MarketSnapshotRepository;
import com.pulse.backend.stock.Stock;
import com.pulse.backend.stock.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SignalService {

    private final MarketSnapshotRepository snapshotRepository;
    private final StockRepository stockRepository;
    private final PriceAnomalyCalculator calculator;
    private final MarketSignalRepository signalRepository;
    private final VolumeAnomalyCalculator volumeAnomalyCalculator;
    private final RelativePerformanceCalculator relativePerformanceCalculator;

    public SignalService(
            MarketSnapshotRepository snapshotRepository,
            StockRepository stockRepository,
            PriceAnomalyCalculator calculator,
            MarketSignalRepository signalRepository,
            VolumeAnomalyCalculator volumeAnomalyCalculator,
            RelativePerformanceCalculator relativePerformanceCalculator) {
        this.snapshotRepository = snapshotRepository;
        this.stockRepository = stockRepository;
        this.calculator = calculator;
        this.signalRepository = signalRepository;
        this.volumeAnomalyCalculator = volumeAnomalyCalculator;
        this.relativePerformanceCalculator = relativePerformanceCalculator;
    }

    @Transactional
    public PriceAnomalyResult calculatePriceAnomaly(
            String symbol,
            BigDecimal currentPrice) {
        String normalizedSymbol = symbol.toUpperCase();

        List<MarketSnapshot> snapshots = snapshotRepository.findHistoricalSnapshots(
                normalizedSymbol,
                OffsetDateTime.now().minusHours(24));

        if (snapshots.size() < 3) {
            throw new IllegalArgumentException(
                    "Not enough historical data for " + normalizedSymbol);
        }

        List<BigDecimal> historicalPrices = snapshots.stream()
                .map(MarketSnapshot::getPrice)
                .toList();

        PriceAnomalyResult result = calculator.calculate(
                historicalPrices.reversed(),
                currentPrice);

        Stock stock = stockRepository.findById(normalizedSymbol)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown stock: " + normalizedSymbol));

        MarketSignal signal = new MarketSignal(
                stock,
                SignalType.PRICE_ANOMALY,
                result.currentReturn(),
                result.score(),
                """
                        {"zScore": %s, "historicalVolatility": %s}
                        """.formatted(
                        result.zScore(),
                        result.historicalVolatility()),
                OffsetDateTime.now());

        signalRepository.save(signal);

        return result;
    }

    @Transactional
    public VolumeAnomalyResult calculateVolumeAnomaly(
            String symbol,
            Long currentVolume) {
        String normalizedSymbol = symbol.toUpperCase();

        List<MarketSnapshot> snapshots = snapshotRepository.findHistoricalSnapshots(
                normalizedSymbol,
                OffsetDateTime.now().minusHours(24));

        if (snapshots.size() < 3) {
            throw new IllegalArgumentException(
                    "Not enough historical data for " + normalizedSymbol);
        }

        VolumeAnomalyResult result = volumeAnomalyCalculator.calculate(
                snapshots,
                currentVolume);

        Stock stock = stockRepository.findById(normalizedSymbol)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown stock: " + normalizedSymbol));

        MarketSignal signal = new MarketSignal(
                stock,
                SignalType.VOLUME_ANOMALY,
                result.volumeRatio(),
                result.score(),
                """
                        {"currentVolume": %s, "historicalAverageVolume": %s}
                        """.formatted(
                        result.currentVolume(),
                        result.historicalAverageVolume()),
                OffsetDateTime.now());

        signalRepository.save(signal);

        return result;
    }

    @Transactional
    public RelativePerformanceResult calculateRelativePerformance(
            String symbol,
            BigDecimal currentPrice,
            BigDecimal benchmarkReturn) {
        String normalizedSymbol = symbol.toUpperCase();

        List<MarketSnapshot> snapshots = snapshotRepository.findHistoricalSnapshots(
                normalizedSymbol,
                OffsetDateTime.now().minusHours(24));

        if (snapshots.size() < 3) {
            throw new IllegalArgumentException(
                    "Not enough historical data for " + normalizedSymbol);
        }

        // Most recent historical price is our comparison price.
        BigDecimal previousPrice = snapshots.get(0).getPrice();

        RelativePerformanceResult result = relativePerformanceCalculator.calculate(
                currentPrice,
                previousPrice,
                benchmarkReturn);

        Stock stock = stockRepository.findById(normalizedSymbol)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown stock: " + normalizedSymbol));

        MarketSignal signal = new MarketSignal(
                stock,
                SignalType.RELATIVE_PERFORMANCE,
                result.divergence(),
                result.score(),
                """
                        {"stockReturn": %s, "benchmarkReturn": %s}
                        """.formatted(
                        result.stockReturn(),
                        result.benchmarkReturn()),
                OffsetDateTime.now());

        signalRepository.save(signal);

        return result;
    }

    public String debug(String symbol, BigDecimal currentPrice, Long currentVolume, BigDecimal benchmarkReturn) {
    var price = calculatePriceAnomaly(symbol, currentPrice);
    var volume = calculateVolumeAnomaly(symbol, currentVolume);
    var relative = calculateRelativePerformance(symbol, currentPrice, benchmarkReturn);
    return """
            priceScore=%s,zScore=%s,currentReturn=%s
            volumeScore=%s,volumeRatio=%s
            relativeScore=%s,divergence=%s
            """.formatted(
            price.score(), price.zScore(), price.currentReturn(),
            volume.score(), volume.volumeRatio(),
            relative.score(), relative.divergence());
    }

    public List<MarketSignal> getSignals(String symbol) {
        return signalRepository.findByStockSymbolOrderByTimestampDesc(symbol.toUpperCase());
        }
}