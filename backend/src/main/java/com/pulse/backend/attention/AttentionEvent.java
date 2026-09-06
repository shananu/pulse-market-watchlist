package com.pulse.backend.attention;

import com.pulse.backend.stock.Stock;
import com.pulse.backend.user.User;
import com.pulse.backend.market.MarketEvent;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "attention_events")
public class AttentionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private MarketEvent event;

    @Column(name = "attention_score", nullable = false)
    private BigDecimal attentionScore;

    @Column(nullable = false)
    private String status;

    @Column(name = "first_detected_at", nullable = false)
    private OffsetDateTime firstDetectedAt;

    @Column(name = "surfaced_at")
    private OffsetDateTime surfacedAt;

    @Column(name = "viewed_at")
    private OffsetDateTime viewedAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AttentionEvent() {
    }

    public AttentionEvent(
            User user,
            Stock stock,
            BigDecimal attentionScore) {
        this.user = user;
        this.stock = stock;
        this.attentionScore = attentionScore;
        this.status = "DETECTED";

        OffsetDateTime now = OffsetDateTime.now();

        this.firstDetectedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Stock getStock() {
        return stock;
    }

    public MarketEvent getEvent() {
        return event;
    }

    public BigDecimal getAttentionScore() {
        return attentionScore;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getFirstDetectedAt() {
        return firstDetectedAt;
    }

    public OffsetDateTime getSurfacedAt() {
        return surfacedAt;
    }

    public OffsetDateTime getViewedAt() {
        return viewedAt;
    }

    public OffsetDateTime getReviewedAt() {
        return reviewedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateScore(BigDecimal newScore) {
        this.attentionScore = newScore;
        this.updatedAt = OffsetDateTime.now();
    }

    public void resurface() {
        this.status = "SURFACED";
        this.surfacedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void markViewed() {
        this.status = "VIEWED";
        this.viewedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void markReviewed() {
        this.status = "REVIEWED";
        this.reviewedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
}