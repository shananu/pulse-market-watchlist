package com.pulse.backend.market.provider;

import java.util.List;

public interface MarketDataProvider {

    List<MarketDataPoint> getLatest(String symbol);

    List<MarketDataPoint> getLatest(List<String> symbols);
}