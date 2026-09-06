package com.pulse.backend.signal;

import com.pulse.backend.stock.Stock;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "market_signals")
public class MarketSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "signal_type", nullable = false)
    private SignalType signalType;

    private BigDecimal value;

    private BigDecimal score;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected MarketSignal() {
    }

    public MarketSignal(
            Stock stock,
            SignalType signalType,
            BigDecimal value,
            BigDecimal score,
            String metadata,
            OffsetDateTime timestamp
    ) {
        this.stock = stock;
        this.signalType = signalType;
        this.value = value;
        this.score = score;
        this.metadata = metadata;
        this.timestamp = timestamp;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public SignalType getSignalType() {
        return signalType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getMetadata() {
        return metadata;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}