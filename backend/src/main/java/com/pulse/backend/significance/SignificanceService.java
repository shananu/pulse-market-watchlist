package com.pulse.backend.significance;

import com.pulse.backend.attention.AttentionEvent;
import com.pulse.backend.attention.AttentionService;
import com.pulse.backend.signal.PriceAnomalyResult;
import com.pulse.backend.signal.RelativePerformanceResult;
import com.pulse.backend.signal.SignalService;
import com.pulse.backend.signal.VolumeAnomalyResult;
import org.springframework.stereotype.Service;
import com.pulse.backend.market.provider.DemoMarketDataProvider;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SignificanceService {

        private final SignalService signalService;
        private final SignificanceCalculator significanceCalculator;
        private final AttentionService attentionService;
        private final DemoMarketDataProvider marketDataProvider;

        public SignificanceService(
                        SignalService signalService,
                        SignificanceCalculator significanceCalculator,
                        AttentionService attentionService,
                        DemoMarketDataProvider marketDataProvider) {

                this.signalService = signalService;
                this.significanceCalculator = significanceCalculator;
                this.attentionService = attentionService;
                this.marketDataProvider = marketDataProvider;
        }

        @Transactional
        public SignificanceResult evaluate(
                        String symbol,
                        BigDecimal currentPrice,
                        Long currentVolume,
                        BigDecimal benchmarkReturn) {

                PriceAnomalyResult price = signalService.calculatePriceAnomaly(
                                symbol,
                                currentPrice);

                VolumeAnomalyResult volume = signalService.calculateVolumeAnomaly(
                                symbol,
                                currentVolume);

                RelativePerformanceResult relative = signalService.calculateRelativePerformance(
                                symbol,
                                currentPrice,
                                benchmarkReturn);

                SignificanceResult significance = significanceCalculator.calculate(
                                price.score(),
                                relative.score(),
                                BigDecimal.ZERO,
                                volume.score(),
                                BigDecimal.ZERO);

                if ("SIGNIFICANT".equals(significance.band())) {
                        attentionService.createFromSignificance(
                                        symbol,
                                        significance);
                }

                return significance;
        }

        @Transactional
        public SignificanceResult evaluateCurrent(String symbol) {

                var point = marketDataProvider.getLatest(symbol).get(0);
                System.out.println("DEMO EVALUATE: minute=" + marketDataProvider.getDemoMinute()
                                + " price=" + point.price() + " volume=" + point.volume());
                                
                return evaluate(point.symbol(), point.price(), point.volume(), new BigDecimal("0.005"));
        }

        public String debug(String symbol, BigDecimal price, Long volume, BigDecimal benchmark) {
                return signalService.debug(symbol, price, volume, benchmark);
        }
}