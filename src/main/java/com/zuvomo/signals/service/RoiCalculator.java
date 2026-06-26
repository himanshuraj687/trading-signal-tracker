package com.zuvomo.signals.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.entity.TradingSignal;

@Component
public class RoiCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public BigDecimal calculate(TradingSignal signal, BigDecimal currentPrice) {
        BigDecimal delta = signal.getDirection() == Direction.BUY
                ? currentPrice.subtract(signal.getEntryPrice())
                : signal.getEntryPrice().subtract(currentPrice);

        return delta
                .divide(signal.getEntryPrice(), 6, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
