package com.zuvomo.signals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zuvomo.signals.entity.TradingSignal;

public interface TradingSignalRepository extends JpaRepository<TradingSignal, Long> {
}
