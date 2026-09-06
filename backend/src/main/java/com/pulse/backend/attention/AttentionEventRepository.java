package com.pulse.backend.attention;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AttentionEventRepository extends JpaRepository<AttentionEvent, Long> {

        @Query("""
                        SELECT ae
                        FROM AttentionEvent ae
                        JOIN FETCH ae.stock
                        WHERE ae.user.id = :userId
                        ORDER BY ae.attentionScore DESC
                        """)
        List<AttentionEvent> findByUserIdOrderByAttentionScoreDesc(
                        @Param("userId") Long userId);

        // @Query("""
        // SELECT ae
        // FROM AttentionEvent ae
        // WHERE ae.user.id = :userId
        // AND ae.stock.symbol = :symbol
        // AND ae.status IN ('DETECTED', 'SURFACED', 'VIEWED')
        // ORDER BY ae.createdAt DESC
        // """)
        // List<AttentionEvent> findActiveByUserAndSymbol(
        // @Param("userId") Long userId,
        // @Param("symbol") String symbol);

        @Query("""
                        SELECT ae FROM AttentionEvent ae
                        JOIN FETCH ae.stock
                        WHERE ae.user.id = :userId AND ae.stock.symbol = :symbol
                        AND ae.status IN ('DETECTED', 'SURFACED', 'VIEWED')
                        ORDER BY ae.createdAt DESC
                        """)
        List<AttentionEvent> findActiveByUserAndSymbol(
                        @Param("userId") Long userId, @Param("symbol") String symbol);

        @Query("SELECT ae FROM AttentionEvent ae JOIN FETCH ae.stock WHERE ae.id = :id")
        Optional<AttentionEvent> findByIdWithStock(@Param("id") Long id);

        @Query("""
                        SELECT ae FROM AttentionEvent ae
                        JOIN FETCH ae.stock
                        WHERE ae.user.id = :userId
                        AND ae.status IN ('DETECTED','SURFACED','VIEWED')
                        AND (ae.createdAt > :since OR ae.surfacedAt > :since)
                        ORDER BY ae.attentionScore DESC
                        """)
        List<AttentionEvent> findNewSince(
                        @Param("userId") Long userId,
                        @Param("since") OffsetDateTime since);
}