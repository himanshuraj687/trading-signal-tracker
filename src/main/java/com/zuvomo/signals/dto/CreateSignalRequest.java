package com.zuvomo.signals.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.zuvomo.signals.entity.Direction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateSignalRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Z0-9]{5,20}$", message = "symbol must be an uppercase Binance pair such as BTCUSDT")
        String symbol,

        @NotNull
        Direction direction,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal entryPrice,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal stopLoss,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal targetPrice,

        @NotNull
        Instant entryTime,

        @NotNull
        Instant expiryTime
) {
}
