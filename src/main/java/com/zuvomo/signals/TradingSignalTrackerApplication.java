package com.zuvomo.signals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TradingSignalTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingSignalTrackerApplication.class, args);
    }
}
