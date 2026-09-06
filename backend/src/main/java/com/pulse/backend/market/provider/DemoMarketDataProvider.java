package com.pulse.backend.market.provider;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
public class DemoMarketDataProvider implements MarketDataProvider {

    private static final Map<String, BigDecimal> BASE_PRICES = Map.of(
            "HDFCBANK", new BigDecimal("1756.00"),
            "RELIANCE", new BigDecimal("2900.00"),
            "INFY", new BigDecimal("1500.00"),
            "TCS", new BigDecimal("4100.00"),
            "ICICIBANK", new BigDecimal("1350.00"),
            "SBIN", new BigDecimal("820.00")
    );

    private int demoMinute = 0;

    @Override
    public List<MarketDataPoint> getLatest(String symbol) {

        symbol = symbol.toUpperCase();

        BigDecimal basePrice = BASE_PRICES.get(symbol);

        if (basePrice == null) {
            throw new IllegalArgumentException(
                    "No demo market data for: " + symbol
            );
        }

        BigDecimal price = getScenarioPrice(
                symbol,
                basePrice,
                demoMinute
        );

        long volume = getScenarioVolume(
                symbol,
                demoMinute
        );

        return List.of(
                new MarketDataPoint(
                        symbol,
                        price,
                        volume,
                        OffsetDateTime.now()
                )
        );
    }

    @Override
    public List<MarketDataPoint> getLatest(List<String> symbols) {

        return symbols.stream()
                .map(this::getLatest)
                .flatMap(List::stream)
                .toList();
    }

    public void setDemoMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(
                    "Demo minute must be between 0 and 59"
            );
        }

        this.demoMinute = minute;
    }

    public int getDemoMinute() {
        return demoMinute;
    }

    private BigDecimal getScenarioPrice(
            String symbol,
            BigDecimal basePrice,
            int minute
    ) {
        if (!symbol.equals("HDFCBANK")) {
            return basePrice;
        }

        if (minute < 10) {
            return basePrice;
        }

        if (minute < 20) {
            return basePrice.multiply(new BigDecimal("0.989"));
        }

        if (minute < 40) {
            return basePrice.multiply(new BigDecimal("0.976"));
        }

        return basePrice.multiply(new BigDecimal("0.968"));
    }

    private long getScenarioVolume(
            String symbol,
            int minute
    ) {
        if (!symbol.equals("HDFCBANK")) {
            return 1_000_000L;
        }

        if (minute < 10) {
            return 1_000_000L;
        }

        if (minute < 20) {
            return 1_500_000L;
        }

        if (minute < 40) {
            return 2_200_000L;
        }

        return 2_700_000L;
    }
}