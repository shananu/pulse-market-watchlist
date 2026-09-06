package com.pulse.backend.user;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_checkpoints")
public class UserCheckpoint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private OffsetDateTime lastCheckedAt;

    public UserCheckpoint() {}

    public UserCheckpoint(User user, OffsetDateTime lastCheckedAt) {
        this.user = user;
        this.lastCheckedAt = lastCheckedAt;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public OffsetDateTime getLastCheckedAt() { return lastCheckedAt; }
    public void setLastCheckedAt(OffsetDateTime lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }
}