package com.pulse.backend.market;

import com.pulse.backend.stock.Stock;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "market_events")
public class MarketEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol", nullable = false)
    private Stock stock;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;

    private String source;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected MarketEvent() {
    }

    public MarketEvent(
            Stock stock,
            String eventType,
            String title,
            String description,
            OffsetDateTime eventTime,
            String source,
            String sourceUrl
    ) {
        this.stock = stock;
        this.eventType = eventType;
        this.title = title;
        this.description = description;
        this.eventTime = eventTime;
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getEventTime() {
        return eventTime;
    }

    public String getSource() {
        return source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}