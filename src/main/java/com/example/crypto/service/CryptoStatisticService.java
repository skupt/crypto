package com.example.crypto.service;

import com.example.crypto.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@PropertySource("classpath:application.properties")
public class CryptoStatisticService {
    @Value("crypto.cash.size")
    private String cacheSizeStr = "1000";
    private CurrencyCache<CacheKey, CryptoCurrencyStatistic> cache = new CurrencyCache<>(Integer.parseInt(cacheSizeStr));

    /**
     * Check cash, if hit is present then returns statistic from cache else  calculate and return new statistic.
     *
     * @param currency
     * @return CryptoCurrencyStatistic for whole available period
     */
    public CryptoCurrencyStatistic getOrCalculateStatisticForWholeTime(CryptoCurrency currency) {
        CacheKey cacheKey = new CacheKey(currency.getSymbol(), LocalDateTime.MIN, Duration.between(LocalDateTime.MAX,
                LocalDateTime.MIN));
        CryptoCurrencyStatistic statistic = cache.get(cacheKey);
        if (statistic == null) {
            statistic = calculateStatistic(currency, LocalDateTime.MIN, Duration.between(LocalDateTime.MAX,
                    LocalDateTime.MIN));
            cache.put(cacheKey, statistic);
        }
        return statistic;
    }

    public CryptoCurrencyStatistic getOrCalculateStatistic(CryptoCurrency currency, LocalDateTime fromInclusive,
                                                           Duration duration) {
        CacheKey cacheKey = new CacheKey(currency.getSymbol(), fromInclusive, duration);
        CryptoCurrencyStatistic statistic = cache.get(cacheKey);
        if (statistic == null) {
            statistic = calculateStatistic(currency, fromInclusive, duration);
            cache.put(cacheKey, statistic);
        }
        return statistic;

    }

    public CryptoCurrencyStatistic calculateStatistic(CryptoCurrency currency, LocalDateTime fromInclusive, Duration duration) {
        SortedSet<TimedValue> filteredValues = currency.getValues()
                .subSet((new TimedValue(fromInclusive, null)),
                        new TimedValue(fromInclusive.plus(duration), null));

        CryptoCurrencyStatistic statistic = new CryptoCurrencyStatistic();
        statistic.setOldest(Collections.max(filteredValues));
        statistic.setNewest(Collections.min(filteredValues));
        Comparator<TimedValue> byPriceComparator = Comparator.comparingDouble(t -> t.getPrice());
        SortedSet<TimedValue> orderedByPriceTimedValues = new TreeSet<>(byPriceComparator);
        orderedByPriceTimedValues.addAll(filteredValues);
        statistic.setMin(Collections.min(orderedByPriceTimedValues));
        statistic.setMax(Collections.max(orderedByPriceTimedValues));

        return statistic;
    }
}
