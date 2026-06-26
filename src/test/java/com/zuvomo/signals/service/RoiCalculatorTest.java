package com.zuvomo.signals.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.entity.TradingSignal;

class RoiCalculatorTest {

    private final RoiCalculator calculator = new RoiCalculator();

    @Test
    void calculatesBuyRoi() {
        TradingSignal signal = signal(Direction.BUY);

        BigDecimal roi = calculator.calculate(signal, new BigDecimal("112.345"));

        assertThat(roi).isEqualByComparingTo("12.35");
    }

    @Test
    void calculatesSellRoi() {
        TradingSignal signal = signal(Direction.SELL);

        BigDecimal roi = calculator.calculate(signal, new BigDecimal("87.655"));

        assertThat(roi).isEqualByComparingTo("12.35");
    }

    private TradingSignal signal(Direction direction) {
        TradingSignal signal = new TradingSignal();
        signal.setDirection(direction);
        signal.setEntryPrice(new BigDecimal("100"));
        return signal;
    }
}
