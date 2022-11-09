package com.example.crypto.model;

import lombok.Data;

import java.util.TreeSet;

/**
 * Class is a container for all initial date about prices changing in time for particular cryptocurrency
 */
@Data
public class CryptoCurrency {
    private String symbol;

    private TreeSet<TimedValue> values = new TreeSet<>();

}
