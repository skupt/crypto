package com.example.crypto.model;

import lombok.Data;

import java.util.TreeSet;

@Data
public class CryptoCurrency {
    private String symbol;

    private TreeSet<TimedValue> values = new TreeSet<>();

}
