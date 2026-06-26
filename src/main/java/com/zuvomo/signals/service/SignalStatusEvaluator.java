package com.zuvomo.signals.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;

import org.springframework.stereotype.Component;

import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.entity.SignalStatus;
import com.zuvomo.signals.entity.TradingSignal;

@Component
public class SignalStatusEvaluator {

    private static final EnumSet<SignalStatus> FINAL_STATUSES = EnumSet.of(
            SignalStatus.TARGET_HIT,
            SignalStatus.STOPLOSS_HIT,
            SignalStatus.EXPIRED
    );

    private final Clock clock;
    private final RoiCalculator roiCalculator;

    public SignalStatusEvaluator(Clock clock, RoiCalculator roiCalculator) {
        this.clock = clock;
        this.roiCalculator = roiCalculator;
    }

    public TradingSignal evaluate(TradingSignal signal, BigDecimal currentPrice) {
        if (FINAL_STATUSES.contains(signal.getStatus())) {
            return signal;
        }

        SignalStatus nextStatus = nextStatus(signal, currentPrice);
        if (nextStatus != SignalStatus.OPEN) {
            signal.setStatus(nextStatus);
            signal.setRealizedRoi(roiCalculator.calculate(signal, currentPrice));
        }
        return signal;
    }

    private SignalStatus nextStatus(TradingSignal signal, BigDecimal currentPrice) {
        if (signal.getDirection() == Direction.BUY) {
            if (currentPrice.compareTo(signal.getTargetPrice()) >= 0) {
                return SignalStatus.TARGET_HIT;
            }
            if (currentPrice.compareTo(signal.getStopLoss()) <= 0) {
                return SignalStatus.STOPLOSS_HIT;
            }
        } else {
            if (currentPrice.compareTo(signal.getTargetPrice()) <= 0) {
                return SignalStatus.TARGET_HIT;
            }
            if (currentPrice.compareTo(signal.getStopLoss()) >= 0) {
                return SignalStatus.STOPLOSS_HIT;
            }
        }

        if (Instant.now(clock).isAfter(signal.getExpiryTime())) {
            return SignalStatus.EXPIRED;
        }
        return SignalStatus.OPEN;
    }
}
