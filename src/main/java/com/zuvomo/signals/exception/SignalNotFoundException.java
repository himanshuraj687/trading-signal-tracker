package com.zuvomo.signals.exception;

public class SignalNotFoundException extends RuntimeException {

    public SignalNotFoundException(Long id) {
        super("Signal not found: " + id);
    }
}
