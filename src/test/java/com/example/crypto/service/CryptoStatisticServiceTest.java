package com.example.crypto.service;

import com.example.crypto.model.CryptoCurrencyStatistic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptoStatisticServiceTest {
    private static Path projectRoot = Paths.get(System.getProperty("user.dir"));
    private static Path pricesDir = projectRoot.resolve("src/test/resources/prices"); //src/test/resources/prices
    private CryptoStatisticService statisticService;

    @BeforeEach
    public void setUp() throws IOException {
        statisticService = new CryptoStatisticService();
        statisticService.setCacheSizeStr("100");
        statisticService.setPathToCryptoPricesFolder(pricesDir.toString());
        statisticService.init();
    }

    @Test
    public void shouldReturnCorrectStatisticForWholePeriodForEthSymbolWhenCallCalculateStatistic() throws IOException {
        CryptoCurrencyStatistic statistic = statisticService.calculateStatistic("ETH",
                LocalDateTime.of(2000, 1, 1, 0, 0),
                Duration.ofDays(36500));
        System.out.println(statistic);
        assertEquals(4050, statistic.getOldest().getPrice());
        assertEquals(3950, statistic.getNewest().getPrice());
        assertEquals(3000, statistic.getMin().getPrice());
        assertEquals(5000, statistic.getMax().getPrice());
    }

    @Test
    public void shouldReturnCorrectStatisticWhenCallGetOrCalculateStatisticForWholeTime() throws IOException {
        CryptoCurrencyStatistic statistic = statisticService.getOrCalculateStatisticForWholeTime("ETH");
        System.out.println(statistic);
        assertEquals(4050, statistic.getOldest().getPrice());
        assertEquals(3950, statistic.getNewest().getPrice());
        assertEquals(3000, statistic.getMin().getPrice());
        assertEquals(5000, statistic.getMax().getPrice());
    }

}
