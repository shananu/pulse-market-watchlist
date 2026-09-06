package com.pulse.backend.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistStockRepository
        extends JpaRepository<WatchlistStock, Long> {

    @Query("""
        SELECT ws
        FROM WatchlistStock ws
        JOIN FETCH ws.stock
        WHERE ws.watchlist.id = :watchlistId
        """)
    List<WatchlistStock> findByWatchlistId(
            @Param("watchlistId") Long watchlistId
    );

    Optional<WatchlistStock> findByWatchlistIdAndStockSymbol(
            Long watchlistId,
            String symbol
    );

    void deleteByWatchlistIdAndStockSymbol(
            Long watchlistId,
            String symbol
    );

    long countByWatchlistId(Long watchlistId);
}