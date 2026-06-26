package com.zuvomo.signals.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuvomo.signals.dto.CreateSignalRequest;
import com.zuvomo.signals.entity.SignalStatus;
import com.zuvomo.signals.entity.TradingSignal;
import com.zuvomo.signals.exception.SignalNotFoundException;
import com.zuvomo.signals.repository.TradingSignalRepository;

@Service
public class TradingSignalService {

    private final TradingSignalRepository repository;
    private final SignalValidationService validationService;
    private final BinancePriceClient priceClient;
    private final SignalStatusEvaluator statusEvaluator;

    public TradingSignalService(
            TradingSignalRepository repository,
            SignalValidationService validationService,
            BinancePriceClient priceClient,
            SignalStatusEvaluator statusEvaluator
    ) {
        this.repository = repository;
        this.validationService = validationService;
        this.priceClient = priceClient;
        this.statusEvaluator = statusEvaluator;
    }

    @Transactional
    public TradingSignal create(CreateSignalRequest request) {
        validationService.validate(request);
        TradingSignal signal = new TradingSignal();
        signal.setSymbol(request.symbol());
        signal.setDirection(request.direction());
        signal.setEntryPrice(request.entryPrice());
        signal.setStopLoss(request.stopLoss());
        signal.setTargetPrice(request.targetPrice());
        signal.setEntryTime(request.entryTime());
        signal.setExpiryTime(request.expiryTime());
        signal.setStatus(SignalStatus.OPEN);
        return repository.save(signal);
    }

    @Transactional
    public List<TradingSignal> findAllAndEvaluate() {
        return repository.findAll().stream()
                .map(this::evaluateIfOpen)
                .toList();
    }

    @Transactional
    public void evaluateOpenSignals() {
        repository.findByStatus(SignalStatus.OPEN)
                .forEach(this::evaluateIfOpen);
    }

    @Transactional
    public TradingSignal findByIdAndEvaluate(Long id) {
        return evaluateIfOpen(findById(id));
    }

    @Transactional
    public void delete(Long id) {
        TradingSignal signal = findById(id);
        repository.delete(signal);
    }

    private TradingSignal findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new SignalNotFoundException(id));
    }

    private TradingSignal evaluateIfOpen(TradingSignal signal) {
        if (signal.getStatus() != SignalStatus.OPEN) {
            return signal;
        }
        BigDecimal currentPrice = priceClient.getCurrentPrice(signal.getSymbol());
        return statusEvaluator.evaluate(signal, currentPrice);
    }
}
