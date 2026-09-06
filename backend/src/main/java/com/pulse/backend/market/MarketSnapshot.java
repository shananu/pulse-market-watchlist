package com.pulse.backend.market;

import com.pulse.backend.stock.Stock;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "market_snapshots")
public class MarketSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "symbol", nullable = false)
    private Stock stock;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    private Long volume;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public MarketSnapshot() {
    }

    public MarketSnapshot(
            Stock stock,
            BigDecimal price,
            Long volume,
            OffsetDateTime timestamp,
            String source
    ) {
        this.stock = stock;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
        this.source = source;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getVolume() {
        return volume;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}