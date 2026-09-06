package com.pulse.backend.attention;

import com.pulse.backend.significance.SignificanceResult;
import com.pulse.backend.stock.Stock;
import com.pulse.backend.stock.StockRepository;
import com.pulse.backend.user.User;
import com.pulse.backend.user.UserRepository;
import com.pulse.backend.user.UserCheckpoint;
import com.pulse.backend.user.UserCheckpointRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AttentionService {

        private static final String DEMO_EMAIL = "demo@pulse.app";

        private final AttentionEventRepository attentionEventRepository;
        private final UserRepository userRepository;
        private final StockRepository stockRepository;
        private final UserCheckpointRepository checkpointRepository;

        public AttentionService(
                        AttentionEventRepository attentionEventRepository,
                        UserRepository userRepository,
                        StockRepository stockRepository,
                        UserCheckpointRepository checkpointRepository) {
                this.attentionEventRepository = attentionEventRepository;
                this.userRepository = userRepository;
                this.stockRepository = stockRepository;
                this.checkpointRepository = checkpointRepository;
        }

        @Transactional
        public AttentionEvent createAttentionEvent(
                        String symbol,
                        BigDecimal attentionScore) {
                User user = userRepository.findByEmail(DEMO_EMAIL)
                                .orElseThrow(() -> new IllegalArgumentException("Demo user not found"));

                Stock stock = stockRepository.findById(symbol.toUpperCase())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Unknown stock: " + symbol));

                AttentionEvent attentionEvent = new AttentionEvent(
                                user,
                                stock,
                                attentionScore);

                return attentionEventRepository.save(attentionEvent);
        }

        @Transactional
        public AttentionEvent createFromSignificance(String symbol, SignificanceResult significance) {
                if (!"SIGNIFICANT".equals(significance.band()))
                        return null;

                User user = userRepository.findByEmail(DEMO_EMAIL)
                                .orElseThrow(() -> new RuntimeException("Demo user not found"));

                String normalizedSymbol = symbol.toUpperCase();

                List<AttentionEvent> active = attentionEventRepository.findActiveByUserAndSymbol(
                                user.getId(), normalizedSymbol);

                if (!active.isEmpty()) {
                        AttentionEvent existing = active.get(0);

                        if (significance.score().compareTo(existing.getAttentionScore()) >= 0) {
                                existing.updateScore(significance.score());
                                existing.resurface();
                        }

                        return existing;
                }

                return createAttentionEvent(normalizedSymbol, significance.score());
        }

        @Transactional(readOnly = true)
        public List<AttentionEvent> getAttentionEvents() {
                User user = userRepository.findByEmail(DEMO_EMAIL)
                                .orElseThrow(() -> new RuntimeException("Demo user not found"));

                return attentionEventRepository.findByUserIdOrderByAttentionScoreDesc(user.getId());
        }

        @Transactional
        public AttentionEvent markViewed(Long id) {
                AttentionEvent event = attentionEventRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Attention event not found"));

                event.markViewed();
                return event;
        }

        @Transactional
        public AttentionEvent markReviewed(Long id) {
                AttentionEvent event = attentionEventRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Attention event not found"));

                event.markReviewed();
                return event;
        }

        @Transactional
        public void resetDemo() {
                User user = userRepository.findByEmail(DEMO_EMAIL)
                                .orElseThrow(() -> new RuntimeException("Demo user not found"));

                attentionEventRepository.findByUserIdOrderByAttentionScoreDesc(user.getId())
                                .forEach(attentionEventRepository::delete);

                UserCheckpoint checkpoint = checkpointRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));

                checkpoint.setLastCheckedAt(java.time.OffsetDateTime.now());
                checkpointRepository.save(checkpoint);
        }
}