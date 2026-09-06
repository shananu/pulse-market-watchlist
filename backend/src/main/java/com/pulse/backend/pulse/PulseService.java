package com.pulse.backend.pulse;

import com.pulse.backend.attention.*;
import com.pulse.backend.user.*;
import com.pulse.backend.watchlist.WatchlistRepository;
import com.pulse.backend.watchlist.WatchlistStockRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service
public class PulseService {
    private static final String DEMO_EMAIL = "demo@pulse.app";

    private final UserRepository userRepository;
    private final UserCheckpointRepository checkpointRepository;
    private final AttentionEventRepository attentionRepository;
    private final WatchlistRepository watchlistRepository;
    private final WatchlistStockRepository watchlistStockRepository;

    public PulseService(UserRepository userRepository, UserCheckpointRepository checkpointRepository,
            AttentionEventRepository attentionRepository, WatchlistRepository watchlistRepository,
            WatchlistStockRepository watchlistStockRepository) {
        this.userRepository = userRepository;
        this.checkpointRepository = checkpointRepository;
        this.attentionRepository = attentionRepository;
        this.watchlistRepository = watchlistRepository;
        this.watchlistStockRepository = watchlistStockRepository;
    }

    public PulseResponse getPulse() {
        User user = userRepository.findByEmail(DEMO_EMAIL).orElseThrow();
        UserCheckpoint checkpoint = checkpointRepository.findByUserId(user.getId()).orElseThrow();

        OffsetDateTime lastChecked = checkpoint.getLastCheckedAt();
        if (lastChecked == null) {
            lastChecked = OffsetDateTime.now();
            checkpoint.setLastCheckedAt(lastChecked);
            checkpointRepository.save(checkpoint);
        }
        long hours = Duration.between(lastChecked, OffsetDateTime.now()).toHours();

        List<AttentionEventResponse> items = attentionRepository.findNewSince(user.getId(), lastChecked)
                .stream()
                .map(e -> new AttentionEventResponse(
                        e.getId(), e.getStock().getSymbol(), e.getAttentionScore(), e.getStatus(),
                        e.getFirstDetectedAt(), e.getSurfacedAt(), e.getViewedAt(),
                        e.getReviewedAt(), e.getCreatedAt()))
                .toList();

        long watchlistId = watchlistRepository.findByUserId(user.getId()).stream().findFirst().orElseThrow().getId();
        int unremarkable = (int) watchlistStockRepository.countByWatchlistId(watchlistId) - items.size();
        unremarkable = Math.max(0, unremarkable);

        return new PulseResponse(lastChecked, hours, items.size(), items, unremarkable);
    }

    @Transactional
    public PulseResponse checkPulse() {
        // PulseResponse pulse = getPulse();
        User user = userRepository.findByEmail(DEMO_EMAIL).orElseThrow();
        UserCheckpoint checkpoint = checkpointRepository.findByUserId(user.getId()).orElseThrow();
        checkpoint.setLastCheckedAt(OffsetDateTime.now());
        checkpointRepository.save(checkpoint);
        return getPulse();
    }

    @Transactional
    public PulseResponse resetPulse() {
        User user = userRepository.findByEmail(DEMO_EMAIL).orElseThrow();
        UserCheckpoint checkpoint = checkpointRepository.findByUserId(user.getId()).orElseThrow();
        checkpoint.setLastCheckedAt(OffsetDateTime.now());
        checkpointRepository.save(checkpoint);
        return getPulse();
    }
}