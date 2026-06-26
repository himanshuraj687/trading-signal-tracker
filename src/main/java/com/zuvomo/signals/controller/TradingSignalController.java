package com.zuvomo.signals.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zuvomo.signals.dto.CreateSignalRequest;
import com.zuvomo.signals.dto.SignalResponse;
import com.zuvomo.signals.entity.TradingSignal;
import com.zuvomo.signals.service.TradingSignalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/signals")
public class TradingSignalController {

    private final TradingSignalService signalService;

    public TradingSignalController(TradingSignalService signalService) {
        this.signalService = signalService;
    }

    @PostMapping
    ResponseEntity<SignalResponse> create(@Valid @RequestBody CreateSignalRequest request) {
        TradingSignal created = signalService.create(request);
        return ResponseEntity
                .created(URI.create("/api/signals/" + created.getId()))
                .body(SignalResponse.from(created));
    }

    @GetMapping
    List<SignalResponse> findAll() {
        return signalService.findAllAndEvaluate().stream()
                .map(SignalResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    SignalResponse findById(@PathVariable Long id) {
        return SignalResponse.from(signalService.findByIdAndEvaluate(id));
    }

    @GetMapping("/{id}/status")
    SignalResponse status(@PathVariable Long id) {
        return findById(id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        signalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
