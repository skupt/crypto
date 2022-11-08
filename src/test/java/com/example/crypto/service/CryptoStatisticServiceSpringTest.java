package com.example.crypto.service;

import com.example.crypto.model.CryptoCurrencyStatistic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CryptoStatisticServiceSpringTest {
    @Autowired
    private CryptoStatisticService service;

    @BeforeEach
    public void setUp() throws IOException {
        service.init();
    }

    @Test
    public void shouldReturnCorrectStatisticForWholePeriodFroEthSymbol() throws IOException {
        CryptoCurrencyStatistic statistic = service.calculateStatistic("ETH",
                LocalDateTime.of(2000, 1, 1, 0, 0),
                Duration.ofDays(36500));
        assertEquals(4050, statistic.getOldest().getPrice());
        assertEquals(3950, statistic.getNewest().getPrice());
        assertEquals(3000, statistic.getMin().getPrice());
        assertEquals(5000, statistic.getMax().getPrice());
    }

    @Test
    public void shouldReturnCorrectStatisticWhenCallGetOrCalculateStatisticForWholeTime() throws IOException {
        CryptoCurrencyStatistic statistic = service.getOrCalculateStatisticForWholeTime("ETH");
        System.out.println(statistic);
        assertEquals(4050, statistic.getOldest().getPrice());
        assertEquals(3950, statistic.getNewest().getPrice());
        assertEquals(3000, statistic.getMin().getPrice());
        assertEquals(5000, statistic.getMax().getPrice());
    }

    @Test
    public void shouldReturnCorrectStatisticWhenCallGetOrCalculateStatistic() throws IOException {
        CryptoCurrencyStatistic statistic = service.getOrCalculateStatistic("ETH",
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofDays(36500));
        System.out.println(statistic);
        assertEquals(4050, statistic.getOldest().getPrice());
        assertEquals(3950, statistic.getNewest().getPrice());
        assertEquals(3000, statistic.getMin().getPrice());
        assertEquals(5000, statistic.getMax().getPrice());
    }

    @Test
    public void shouldReturnCorrectOrderedStatisticList() {
        List<CryptoCurrencyStatistic> statisticList = service.calculateDescendingCryptoCurrencyList(
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofDays(365000));
        statisticList.forEach(e -> System.out.println(e.getSymbol() + " : " + e.getNormalizedRange()));
        Map<String, CryptoCurrencyStatistic> map = statisticList.stream().collect(Collectors.toMap(s -> s.getSymbol(), s -> s));
        assertEquals(0.04114398257311577, map.get("BTC").getNormalizedRange(), 0.01);
        assertEquals(0.11507936507936507, map.get("LTC").getNormalizedRange(), 0.01);
        assertEquals(0.6666666666666666, map.get("ETH").getNormalizedRange(), 0.01);
    }

    @Test
    public void shouldReturnCorrectOrderedStatisticListForDayAbsent() {
        List<CryptoCurrencyStatistic> statisticList = service.calculateDescendingCryptoCurrencyList(
                LocalDateTime.of(2022, 6, 1, 0, 0), Duration.ofDays(365000));
        statisticList.forEach(e -> System.out.println(e.getSymbol() + " : " + e.getNormalizedRange()));
        assertEquals(0, statisticList.size());
    }


    @Test
    public void shouldReturnCorrectOrderedStatisticListForDay() {
        List<CryptoCurrencyStatistic> statisticList = service.calculateDescendingCryptoCurrencyList(
                LocalDateTime.of(2022, 1, 10, 0, 0), Duration.ofDays(1));
        assertEquals(0.0038444198296586265, statisticList.get(0).getNormalizedRange(), 0.001);
        assertEquals(0.002354788069073761, statisticList.get(1).getNormalizedRange(), 0.001);
    }
}
