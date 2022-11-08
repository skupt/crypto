package com.example.crypto.dto;

import lombok.Data;

@Data
public class CryptoCurrencyStatisticForAllTime {
    private String symbol;
    private Double oldest;
    private Double newest;
    private Double min;
    private Double max;
}
