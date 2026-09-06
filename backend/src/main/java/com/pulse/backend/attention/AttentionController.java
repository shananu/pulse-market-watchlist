package com.pulse.backend.attention;

import org.springframework.web.bind.annotation.*;
import com.pulse.backend.significance.SignificanceResult;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/attention")
public class AttentionController {

    private final AttentionService attentionService;

    public AttentionController(AttentionService attentionService) {
        this.attentionService = attentionService;
    }

    @PostMapping("/test")
    public AttentionEvent create(@RequestParam String symbol,@RequestParam BigDecimal score) {
        SignificanceResult significance = new SignificanceResult(
                score,
                score.compareTo(BigDecimal.valueOf(70)) >= 0 ? "SIGNIFICANT" : "NOTABLE",
                0,false);
        return attentionService.createFromSignificance(symbol,significance);
    }

    @GetMapping
    public List<AttentionEventResponse> getAll() {
        return attentionService.getAttentionEvents().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/{id}/view")
    public AttentionEventResponse view(@PathVariable Long id) {
        return toResponse(attentionService.markViewed(id));
    }

    @PostMapping("/{id}/review")
    public AttentionEventResponse review(@PathVariable Long id) {
        return toResponse(attentionService.markReviewed(id));
    }

    @PostMapping("/reset")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void reset() {
        attentionService.resetDemo();
    }

    private AttentionEventResponse toResponse(AttentionEvent e) {
        return new AttentionEventResponse(
                e.getId(),e.getStock().getSymbol(),e.getAttentionScore(),
                e.getStatus(),e.getFirstDetectedAt(),e.getSurfacedAt(),
                e.getViewedAt(),e.getReviewedAt(),e.getCreatedAt());
    }
}