package com.pulse.backend.market;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketEventRepository
        extends JpaRepository<MarketEvent, Long> {

    List<MarketEvent> findByStockSymbolOrderByEventTimeDesc(
            String symbol
    );
}