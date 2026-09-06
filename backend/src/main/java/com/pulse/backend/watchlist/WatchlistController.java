package com.pulse.backend.watchlist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<WatchlistResponse> getWatchlists() {
        return watchlistService.getWatchlists()
                .stream()
                .map(WatchlistResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistResponse createWatchlist(
            @Valid @RequestBody CreateWatchlistRequest request
    ) {
        return WatchlistResponse.from(
                watchlistService.createWatchlist(request.name())
        );
    }

    @GetMapping("/{watchlistId}/stocks")
    public List<StockResponse> getStocks(
            @PathVariable Long watchlistId
    ) {
        return watchlistService.getStocks(watchlistId)
                .stream()
                .map(ws -> new StockResponse(
                        ws.getStock().getSymbol(),
                        ws.getStock().getCompanyName(),
                        ws.getStock().getSector(),
                        ws.getStock().getExchange()
                ))
                .toList();
    }

    @PostMapping("/{watchlistId}/stocks/{symbol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addStock(
            @PathVariable Long watchlistId,
            @PathVariable String symbol
    ) {
        watchlistService.addStock(watchlistId, symbol);
    }

    @DeleteMapping("/{watchlistId}/stocks/{symbol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStock(
            @PathVariable Long watchlistId,
            @PathVariable String symbol
    ) {
        watchlistService.removeStock(watchlistId, symbol);
    }

    public record CreateWatchlistRequest(
            @NotBlank(message = "Watchlist name cannot be empty")
            String name
    ) {
    }

    public record WatchlistResponse(
            Long id,
            String name,
            OffsetDateTime createdAt
    ) {
        static WatchlistResponse from(Watchlist watchlist) {
            return new WatchlistResponse(
                    watchlist.getId(),
                    watchlist.getName(),
                    watchlist.getCreatedAt()
            );
        }
    }

    public record StockResponse(
            String symbol,
            String companyName,
            String sector,
            String exchange
    ) {
    }
}