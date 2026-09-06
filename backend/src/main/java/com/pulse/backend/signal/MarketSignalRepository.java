package com.pulse.backend.signal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketSignalRepository
        extends JpaRepository<MarketSignal, Long> {

    List<MarketSignal> findByStockSymbolOrderByTimestampDesc(String symbol);
}