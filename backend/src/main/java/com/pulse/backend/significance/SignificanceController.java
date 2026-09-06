package com.pulse.backend.significance;
import com.pulse.backend.market.provider.DemoMarketDataProvider;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/significance")
public class SignificanceController {

    private final SignificanceService significanceService;
    private final DemoMarketDataProvider marketDataProvider;

    public SignificanceController(SignificanceService significanceService,
                              DemoMarketDataProvider marketDataProvider) {
    this.significanceService = significanceService;
    this.marketDataProvider = marketDataProvider;
}

    @PostMapping("/evaluate")
    public SignificanceResult evaluate(
            @RequestParam String symbol,
            @RequestParam BigDecimal currentPrice,
            @RequestParam Long currentVolume,
            @RequestParam(defaultValue = "0") BigDecimal benchmarkReturn
    ) {
        return significanceService.evaluate(
                symbol,
                currentPrice,
                currentVolume,
                benchmarkReturn
        );
    }

    @PostMapping("/evaluate/{symbol}")
    public SignificanceResult evaluateCurrent(@PathVariable String symbol) {
        return significanceService.evaluateCurrent(symbol);
    }


    @PostMapping("/demo-minute/{minute}")
    public String setDemoMinute(@PathVariable int minute) {
        marketDataProvider.setDemoMinute(minute);
        return "Demo minute set to " + minute;
    }


    @GetMapping("/debug")
public String debug(@RequestParam String symbol,
                    @RequestParam BigDecimal currentPrice,
                    @RequestParam Long currentVolume,
                    @RequestParam(defaultValue = "0") BigDecimal benchmarkReturn) {
    return significanceService.debug(symbol, currentPrice, currentVolume, benchmarkReturn);
}

@GetMapping("/demo-minute")
public int getDemoMinute() {
    return marketDataProvider.getDemoMinute();
}
}