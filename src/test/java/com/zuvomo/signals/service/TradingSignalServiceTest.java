package com.zuvomo.signals.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.entity.SignalStatus;
import com.zuvomo.signals.entity.TradingSignal;
import com.zuvomo.signals.repository.TradingSignalRepository;

@ExtendWith(MockitoExtension.class)
class TradingSignalServiceTest {

    @Mock
    private TradingSignalRepository repository;

    @Mock
    private SignalValidationService validationService;

    @Mock
    private BinancePriceClient priceClient;

    @Mock
    private SignalStatusEvaluator statusEvaluator;

    @InjectMocks
    private TradingSignalService service;

    @Test
    void evaluatesOpenSignalUsingMockedBinancePriceClient() {
        TradingSignal signal = signal(SignalStatus.OPEN);
        when(repository.findById(1L)).thenReturn(Optional.of(signal));
        when(priceClient.getCurrentPrice("BTCUSDT")).thenReturn(new BigDecimal("111"));
        when(statusEvaluator.evaluate(signal, new BigDecimal("111"))).thenReturn(signal);

        TradingSignal result = service.findByIdAndEvaluate(1L);

        assertThat(result).isSameAs(signal);
        verify(priceClient).getCurrentPrice("BTCUSDT");
        verify(statusEvaluator).evaluate(signal, new BigDecimal("111"));
    }

    @Test
    void doesNotCallExternalPriceClientForFinalSignal() {
        TradingSignal signal = signal(SignalStatus.TARGET_HIT);
        when(repository.findById(1L)).thenReturn(Optional.of(signal));

        TradingSignal result = service.findByIdAndEvaluate(1L);

        assertThat(result).isSameAs(signal);
        verify(priceClient, never()).getCurrentPrice("BTCUSDT");
        verify(statusEvaluator, never()).evaluate(signal, new BigDecimal("111"));
    }

    @Test
    void schedulerPathEvaluatesOnlyOpenSignals() {
        TradingSignal openSignal = signal(SignalStatus.OPEN);
        when(repository.findByStatus(SignalStatus.OPEN)).thenReturn(List.of(openSignal));
        when(priceClient.getCurrentPrice("BTCUSDT")).thenReturn(new BigDecimal("111"));
        when(statusEvaluator.evaluate(openSignal, new BigDecimal("111"))).thenReturn(openSignal);

        service.evaluateOpenSignals();

        verify(repository).findByStatus(SignalStatus.OPEN);
        verify(priceClient).getCurrentPrice("BTCUSDT");
        verify(statusEvaluator).evaluate(openSignal, new BigDecimal("111"));
    }

    private TradingSignal signal(SignalStatus status) {
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");
        signal.setDirection(Direction.BUY);
        signal.setEntryPrice(new BigDecimal("100"));
        signal.setStopLoss(new BigDecimal("95"));
        signal.setTargetPrice(new BigDecimal("110"));
        signal.setEntryTime(Instant.parse("2026-06-26T10:00:00Z"));
        signal.setExpiryTime(Instant.parse("2026-06-27T10:00:00Z"));
        signal.setStatus(status);
        return signal;
    }
}
