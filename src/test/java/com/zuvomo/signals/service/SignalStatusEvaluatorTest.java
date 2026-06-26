package com.zuvomo.signals.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.entity.SignalStatus;
import com.zuvomo.signals.entity.TradingSignal;

class SignalStatusEvaluatorTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-26T12:00:00Z"), ZoneOffset.UTC);
    private final SignalStatusEvaluator evaluator = new SignalStatusEvaluator(clock, new RoiCalculator());

    @Test
    void buyTargetHitIsFinal() {
        TradingSignal signal = signal(Direction.BUY);

        evaluator.evaluate(signal, new BigDecimal("111"));

        assertThat(signal.getStatus()).isEqualTo(SignalStatus.TARGET_HIT);
        assertThat(signal.getRealizedRoi()).isEqualByComparingTo("11.00");
    }

    @Test
    void buyStopLossHitIsFinal() {
        TradingSignal signal = signal(Direction.BUY);

        evaluator.evaluate(signal, new BigDecimal("94"));

        assertThat(signal.getStatus()).isEqualTo(SignalStatus.STOPLOSS_HIT);
        assertThat(signal.getRealizedRoi()).isEqualByComparingTo("-6.00");
    }

    @Test
    void sellTargetHitIsFinal() {
        TradingSignal signal = signal(Direction.SELL);

        evaluator.evaluate(signal, new BigDecimal("89"));

        assertThat(signal.getStatus()).isEqualTo(SignalStatus.TARGET_HIT);
        assertThat(signal.getRealizedRoi()).isEqualByComparingTo("11.00");
    }

    @Test
    void sellStopLossHitIsFinal() {
        TradingSignal signal = signal(Direction.SELL);

        evaluator.evaluate(signal, new BigDecimal("106"));

        assertThat(signal.getStatus()).isEqualTo(SignalStatus.STOPLOSS_HIT);
        assertThat(signal.getRealizedRoi()).isEqualByComparingTo("-6.00");
    }

    @Test
    void expiresWhenNoPriceBoundaryWasHit() {
        TradingSignal signal = signal(Direction.BUY);
        signal.setExpiryTime(Instant.parse("2026-06-26T11:59:59Z"));

        evaluator.evaluate(signal, new BigDecimal("100"));

        assertThat(signal.getStatus()).isEqualTo(SignalStatus.EXPIRED);
        assertThat(signal.getRealizedRoi()).isEqualByComparingTo("0.00");
    }

    @Test
    void finalStatusDoesNotChange() {
        TradingSignal signal = signal(Direction.BUY);
        signal.setStatus(SignalStatus.TARGET_HIT);
        signal.setRealizedRoi(new BigDecimal("10.00"));

        evaluator.evaluate(signal, new BigDecimal("90"));

        assertThat(signal.getStatus()).isEqualTo(SignalStatus.TARGET_HIT);
        assertThat(signal.getRealizedRoi()).isEqualByComparingTo("10.00");
    }

    private TradingSignal signal(Direction direction) {
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");
        signal.setDirection(direction);
        signal.setEntryPrice(new BigDecimal("100"));
        if (direction == Direction.BUY) {
            signal.setStopLoss(new BigDecimal("95"));
            signal.setTargetPrice(new BigDecimal("110"));
        } else {
            signal.setStopLoss(new BigDecimal("105"));
            signal.setTargetPrice(new BigDecimal("90"));
        }
        signal.setEntryTime(Instant.parse("2026-06-26T10:00:00Z"));
        signal.setExpiryTime(Instant.parse("2026-06-27T10:00:00Z"));
        signal.setStatus(SignalStatus.OPEN);
        return signal;
    }
}
