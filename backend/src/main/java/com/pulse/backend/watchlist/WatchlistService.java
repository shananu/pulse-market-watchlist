package com.pulse.backend.watchlist;

import com.pulse.backend.stock.Stock;
import com.pulse.backend.stock.StockRepository;
import com.pulse.backend.user.User;
import com.pulse.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private static final String DEMO_EMAIL = "demo@pulse.app";

    private final WatchlistRepository watchlistRepository;
    private final WatchlistStockRepository watchlistStockRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public WatchlistService(
            WatchlistRepository watchlistRepository,
            WatchlistStockRepository watchlistStockRepository,
            StockRepository stockRepository,
            UserRepository userRepository
    ) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistStockRepository = watchlistStockRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
    }

    private User getDemoUser() {
        return userRepository.findByEmail(DEMO_EMAIL)
                .orElseThrow(() -> new IllegalStateException("Demo user not found"));
    }

    public List<Watchlist> getWatchlists() {
        return watchlistRepository.findByUserId(getDemoUser().getId());
    }

    @Transactional
    public Watchlist createWatchlist(String name) {
        User user = getDemoUser();

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Watchlist name cannot be empty");
        }

        return watchlistRepository.save(
                new Watchlist(user, name.trim())
        );
    }

    @Transactional
    public void addStock(Long watchlistId, String symbol) {
        Watchlist watchlist = getUserWatchlist(watchlistId);

        Stock stock = stockRepository.findById(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock not found: " + symbol
                ));

        if (watchlistStockRepository
                .findByWatchlistIdAndStockSymbol(watchlistId, stock.getSymbol())
                .isPresent()) {
            return;
        }

        watchlistStockRepository.save(
                new WatchlistStock(watchlist, stock)
        );
    }

    @Transactional
    public void removeStock(Long watchlistId, String symbol) {
        getUserWatchlist(watchlistId);

        watchlistStockRepository.deleteByWatchlistIdAndStockSymbol(
                watchlistId,
                symbol.toUpperCase()
        );
    }

    @Transactional(readOnly = true)
    public List<WatchlistStock> getStocks(Long watchlistId) {
        getUserWatchlist(watchlistId);

        return watchlistStockRepository.findByWatchlistId(watchlistId);
    }

    private Watchlist getUserWatchlist(Long watchlistId) {
        User user = getDemoUser();

        return watchlistRepository.findById(watchlistId)
                .filter(w -> w.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Watchlist not found"
                ));
    }
}