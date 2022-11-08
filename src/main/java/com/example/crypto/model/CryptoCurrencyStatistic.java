package com.example.crypto.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class CryptoCurrencyStatistic {
    private String symbol;
    private LocalDateTime start;
    private Duration duration;
    private TimedValue oldest;
    private TimedValue newest;
    private TimedValue min;
    private TimedValue max;
    private double normalizedRange;
}
