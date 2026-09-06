package com.pulse.backend.market;

import com.pulse.backend.market.provider.MarketDataPoint;
import com.pulse.backend.market.provider.MarketDataProvider;
import com.pulse.backend.stock.Stock;
import com.pulse.backend.stock.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarketSnapshotService {

    private final MarketDataProvider marketDataProvider;
    private final StockRepository stockRepository;
    private final MarketSnapshotRepository marketSnapshotRepository;

    public MarketSnapshotService(
            MarketDataProvider marketDataProvider,
            StockRepository stockRepository,
            MarketSnapshotRepository marketSnapshotRepository
    ) {
        this.marketDataProvider = marketDataProvider;
        this.stockRepository = stockRepository;
        this.marketSnapshotRepository = marketSnapshotRepository;
    }

    @Transactional
    public List<MarketSnapshot> captureLatest(List<String> symbols) {

        List<MarketDataPoint> points =
                marketDataProvider.getLatest(symbols);

        return points.stream()
                .map(this::saveSnapshot)
                .toList();
    }

    @Transactional
    public MarketSnapshot captureLatest(String symbol) {

        MarketDataPoint point =
                marketDataProvider.getLatest(symbol)
                        .getFirst();

        return saveSnapshot(point);
    }

    private MarketSnapshot saveSnapshot(MarketDataPoint point) {

        Stock stock = stockRepository.findById(point.symbol())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock not found: " + point.symbol()
                ));

        MarketSnapshot snapshot = new MarketSnapshot(
                stock,
                point.price(),
                point.volume(),
                point.timestamp(),
                "DEMO"
        );

        return marketSnapshotRepository.save(snapshot);
    }

    @Transactional(readOnly = true)
    public List<MarketSnapshot> getHistory(String symbol) {

        return marketSnapshotRepository
                .findByStockSymbolOrderByTimestampDesc(
                        symbol.toUpperCase()
                );
    }
}