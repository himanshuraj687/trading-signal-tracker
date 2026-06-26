package com.zuvomo.signals.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.zuvomo.signals.dto.CreateSignalRequest;
import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.exception.InvalidSignalException;

class SignalValidationServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-26T12:00:00Z"), ZoneOffset.UTC);
    private final SignalValidationService service = new SignalValidationService(clock);

    @Test
    void validatesBuySignal() {
        CreateSignalRequest request = request(Direction.BUY, "100", "95", "110");

        assertThatCode(() -> service.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void rejectsBuySignalWithStopLossAboveEntry() {
        CreateSignalRequest request = request(Direction.BUY, "100", "101", "110");

        assertThatThrownBy(() -> service.validate(request))
                .isInstanceOf(InvalidSignalException.class)
                .hasMessageContaining("BUY stop loss");
    }

    @Test
    void validatesSellSignal() {
        CreateSignalRequest request = request(Direction.SELL, "100", "105", "90");

        assertThatCode(() -> service.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void rejectsSellSignalWithTargetAboveEntry() {
        CreateSignalRequest request = request(Direction.SELL, "100", "105", "101");

        assertThatThrownBy(() -> service.validate(request))
                .isInstanceOf(InvalidSignalException.class)
                .hasMessageContaining("SELL target");
    }

    @Test
    void rejectsEntryTimeOlderThanTwentyFourHours() {
        CreateSignalRequest request = new CreateSignalRequest(
                "BTCUSDT",
                Direction.BUY,
                new BigDecimal("100"),
                new BigDecimal("95"),
                new BigDecimal("110"),
                Instant.parse("2026-06-25T11:59:59Z"),
                Instant.parse("2026-06-27T12:00:00Z")
        );

        assertThatThrownBy(() -> service.validate(request))
                .isInstanceOf(InvalidSignalException.class)
                .hasMessageContaining("24 hours");
    }

    @Test
    void rejectsExpiryBeforeEntry() {
        CreateSignalRequest request = new CreateSignalRequest(
                "BTCUSDT",
                Direction.BUY,
                new BigDecimal("100"),
                new BigDecimal("95"),
                new BigDecimal("110"),
                Instant.parse("2026-06-26T12:00:00Z"),
                Instant.parse("2026-06-26T11:59:59Z")
        );

        assertThatThrownBy(() -> service.validate(request))
                .isInstanceOf(InvalidSignalException.class)
                .hasMessageContaining("expiry time");
    }

    private CreateSignalRequest request(Direction direction, String entry, String stopLoss, String target) {
        return new CreateSignalRequest(
                "BTCUSDT",
                direction,
                new BigDecimal(entry),
                new BigDecimal(stopLoss),
                new BigDecimal(target),
                Instant.parse("2026-06-26T10:00:00Z"),
                Instant.parse("2026-06-27T10:00:00Z")
        );
    }
}
