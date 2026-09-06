package com.pulse.backend.watchlist;

import com.pulse.backend.stock.Stock;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
    name = "watchlist_stocks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_watchlist_stock",
            columnNames = {"watchlist_id", "symbol"}
        )
    }
)
public class WatchlistStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private Watchlist watchlist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "symbol", nullable = false)
    private Stock stock;

    @Column(name = "added_at", nullable = false)
    private OffsetDateTime addedAt;

    public WatchlistStock() {
    }

    public WatchlistStock(Watchlist watchlist, Stock stock) {
        this.watchlist = watchlist;
        this.stock = stock;
        this.addedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public Stock getStock() {
        return stock;
    }

    public OffsetDateTime getAddedAt() {
        return addedAt;
    }
}