package com.zuvomo.signals.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SignalEvaluationScheduler {

    private final TradingSignalService signalService;

    public SignalEvaluationScheduler(TradingSignalService signalService) {
        this.signalService = signalService;
    }

    @Scheduled(fixedDelayString = "${signals.evaluation.fixed-delay-ms:60000}")
    public void evaluateOpenSignals() {
        signalService.evaluateOpenSignals();
    }
}
