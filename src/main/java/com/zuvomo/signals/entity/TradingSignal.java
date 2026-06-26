package com.zuvomo.signals.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "trading_signals")
@Check(constraints = "entry_price > 0 and stop_loss > 0 and target_price > 0 and expiry_time > entry_time")
public class TradingSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4)
    private Direction direction;

    @Column(name = "entry_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal entryPrice;

    @Column(name = "stop_loss", nullable = false, precision = 19, scale = 8)
    private BigDecimal stopLoss;

    @Column(name = "target_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal targetPrice;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SignalStatus status = SignalStatus.OPEN;

    @Column(name = "realized_roi", precision = 10, scale = 2)
    private BigDecimal realizedRoi;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public BigDecimal getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(BigDecimal entryPrice) {
        this.entryPrice = entryPrice;
    }

    public BigDecimal getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(BigDecimal stopLoss) {
        this.stopLoss = stopLoss;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public SignalStatus getStatus() {
        return status;
    }

    public void setStatus(SignalStatus status) {
        this.status = status;
    }

    public BigDecimal getRealizedRoi() {
        return realizedRoi;
    }

    public void setRealizedRoi(BigDecimal realizedRoi) {
        this.realizedRoi = realizedRoi;
    }
}
