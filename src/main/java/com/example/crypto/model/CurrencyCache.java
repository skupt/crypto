package com.example.crypto.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public CurrencyCache(int maxSize) {
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (this.size() >= maxSize) return true;
        return false;
    }
}
