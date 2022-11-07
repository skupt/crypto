package com.example.crypto.model;

import lombok.Data;

import java.util.SortedSet;
import java.util.TreeSet;

@Data
public class CryptoCurrency {
    private String symbol;
    private SortedSet<TimedValue> values = new TreeSet<>();

    /**
     * Calculates oldest/newest/min/max for each crypto for the whole particular month
     */
//    public
}
