package com.zuvomo.signals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zuvomo.signals.entity.SignalStatus;
import com.zuvomo.signals.entity.TradingSignal;

public interface TradingSignalRepository extends JpaRepository<TradingSignal, Long> {

    List<TradingSignal> findByStatus(SignalStatus status);
}
