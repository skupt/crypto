package com.example.crypto.model;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;


public final class CacheKey {
    private final String symbol;
    @Getter
    private final LocalDateTime start;
    @Getter
    private final Duration duration;

    public CacheKey(String symbol, LocalDateTime start, Duration duration) {
        this.symbol = symbol;
        this.start = start;
        this.duration = duration;
    }
}
