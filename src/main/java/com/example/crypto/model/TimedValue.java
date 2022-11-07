package com.example.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimedValue implements Comparable<TimedValue> {
    private LocalDateTime localDateTime;
    private Double price;

    @Override
    public int compareTo(TimedValue o) {
        return this.localDateTime.compareTo(o.localDateTime);
    }
}
