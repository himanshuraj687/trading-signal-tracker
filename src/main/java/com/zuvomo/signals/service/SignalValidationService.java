package com.zuvomo.signals.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.zuvomo.signals.dto.CreateSignalRequest;
import com.zuvomo.signals.entity.Direction;
import com.zuvomo.signals.exception.InvalidSignalException;

@Service
public class SignalValidationService {

    private static final Duration MAX_ENTRY_AGE = Duration.ofHours(24);

    private final Clock clock;

    public SignalValidationService(Clock clock) {
        this.clock = clock;
    }

    public void validate(CreateSignalRequest request) {
        validatePrices(request);
        validateTimes(request.entryTime(), request.expiryTime());
    }

    private void validatePrices(CreateSignalRequest request) {
        if (request.direction() == Direction.BUY) {
            if (request.stopLoss().compareTo(request.entryPrice()) >= 0) {
                throw new InvalidSignalException("BUY stop loss must be less than entry price");
            }
            if (request.targetPrice().compareTo(request.entryPrice()) <= 0) {
                throw new InvalidSignalException("BUY target price must be greater than entry price");
            }
            return;
        }

        if (request.stopLoss().compareTo(request.entryPrice()) <= 0) {
            throw new InvalidSignalException("SELL stop loss must be greater than entry price");
        }
        if (request.targetPrice().compareTo(request.entryPrice()) >= 0) {
            throw new InvalidSignalException("SELL target price must be less than entry price");
        }
    }

    private void validateTimes(Instant entryTime, Instant expiryTime) {
        Instant now = Instant.now(clock);
        if (!expiryTime.isAfter(entryTime)) {
            throw new InvalidSignalException("expiry time must be after entry time");
        }
        if (entryTime.isBefore(now.minus(MAX_ENTRY_AGE))) {
            throw new InvalidSignalException("entry time may be at most 24 hours in the past");
        }
    }
}
