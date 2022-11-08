package com.example.crypto.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RangeForPeriod {
    private LocalDateTime dateTime;
    private Duration duration;
    private List<CryptoRange> cryptos;

}
