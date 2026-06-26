package com.zuvomo.signals.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.entity.SignalStatus;
import com.zuvomo.signals.entity.TradingSignal;

public record SignalResponse(
        Long id,
        String symbol,
        Direction direction,
        BigDecimal entryPrice,
        BigDecimal stopLoss,
        BigDecimal targetPrice,
        Instant entryTime,
        Instant expiryTime,
        Instant createdAt,
        SignalStatus status,
        BigDecimal realizedRoi
) {
    public static SignalResponse from(TradingSignal signal) {
        return new SignalResponse(
                signal.getId(),
                signal.getSymbol(),
                signal.getDirection(),
                signal.getEntryPrice(),
                signal.getStopLoss(),
                signal.getTargetPrice(),
                signal.getEntryTime(),
                signal.getExpiryTime(),
                signal.getCreatedAt(),
                signal.getStatus(),
                signal.getRealizedRoi()
        );
    }
}
