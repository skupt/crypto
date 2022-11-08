package com.example.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricedValue implements Comparable<PricedValue> {
    private LocalDateTime localDateTime;
    private Double price;

    @Override
    public int compareTo(PricedValue o) {
        return this.price.compareTo(o.price);
    }
}
