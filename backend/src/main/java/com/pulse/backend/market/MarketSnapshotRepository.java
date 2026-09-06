package com.pulse.backend.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface MarketSnapshotRepository
        extends JpaRepository<MarketSnapshot, Long> {

    List<MarketSnapshot> findByStockSymbolOrderByTimestampDesc(
            String symbol
    );

    @Query("""
        SELECT s
        FROM MarketSnapshot s
        WHERE s.stock.symbol = :symbol
        AND s.timestamp < :before
        AND s.source = 'HISTORICAL'
        ORDER BY s.timestamp DESC
        """)

        List<MarketSnapshot> findHistoricalSnapshots(
                @Param("symbol") String symbol,
                @Param("before") OffsetDateTime before
        );
}