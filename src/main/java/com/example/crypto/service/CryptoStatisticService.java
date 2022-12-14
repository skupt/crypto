package com.example.crypto.service;

import com.example.crypto.exception.CryptoNameValidationException;
import com.example.crypto.helper.CryptoLoader;
import com.example.crypto.model.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:application.properties")
public class CryptoStatisticService {
    @Value("${crypto.cash.size}")
    @Setter
    private String cacheSizeStr = "1000";

    @Value("${crypto.prices.path}")
    @Setter
    private String pathToCryptoPricesFolder = "src/main/resources/prices";
    private CurrencyCache<CacheKey, CryptoCurrencyStatistic> cache = new CurrencyCache<>(Integer.parseInt(cacheSizeStr));
    private Map<String, CryptoCurrency> currencyMap;

    @PostConstruct
    public void init() throws IOException {
        currencyMap = CryptoLoader.loadCryptoPrices(pathToCryptoPricesFolder);
    }

    /**
     * Check cash, if hit is present then returns statistic from cache else  calculate and return new statistic.
     * Actually method return statistic starting from 2000-01-01 00:00 with duration of 36500 days.
     * This period in this app means statistic for period of all time of existing cryptocurrencies.
     *
     * @param currencyName String The name of cryptocurrency (symbol, like 'BTC' etc.)
     * @return CryptoCurrencyStatistic for whole available period
     */
    public CryptoCurrencyStatistic getOrCalculateStatisticForWholeTime(String currencyName) {
        CryptoCurrency currency = currencyMap.get(currencyName);
        CacheKey cacheKey = new CacheKey(currency.getSymbol(), LocalDateTime.of(2000, 1, 1, 0, 0),
                Duration.ofDays(36500));
        CryptoCurrencyStatistic statistic = cache.get(cacheKey);
        if (statistic == null) {
            statistic = calculateStatistic(currencyName, LocalDateTime.of(2000, 1, 1, 0, 0),
                    Duration.ofDays(36500));

            cache.put(cacheKey, statistic);
        }
        return statistic;
    }

    /**
     * Check cash, if hit is present then returns statistic from cache else calculate and return new statistic.
     *
     * @param currencyName  String The name of cryptocurrency (symbol, like 'BTC' etc.)
     * @param fromInclusive LocalDateTime for start period (inclusive)
     * @param duration      Duration for duration of period
     * @return
     */
    public CryptoCurrencyStatistic getOrCalculateStatistic(String currencyName, LocalDateTime fromInclusive,
                                                           Duration duration) {
        CryptoCurrency currency = currencyMap.get(currencyName);
        CacheKey cacheKey = new CacheKey(currency.getSymbol(), fromInclusive, duration);
        CryptoCurrencyStatistic statistic = cache.get(cacheKey);
        if (statistic == null) {
            statistic = calculateStatistic(currencyName, fromInclusive, duration);
            cache.put(cacheKey, statistic);
        }
        return statistic;
    }

    /**
     * The method returns a list of CryptoCurrency statistics for a period of time limited by a starting point in time
     * and a duration.
     *
     * @param fromInclusive LocalDateTime for start period (inclusive)
     * @param duration      Duration for duration of period
     * @return List<CryptoCurrencyStatistic>
     */
    public List<CryptoCurrencyStatistic> calculateDescendingCryptoCurrencyList(LocalDateTime fromInclusive, Duration duration) {
        SortedSet<CryptoCurrencyStatistic> statSortedSet = new TreeSet<>(Comparator.comparing(CryptoCurrencyStatistic::getNormalizedRange).reversed());
        for (CryptoCurrency currency : currencyMap.values()) {
            CryptoCurrencyStatistic ccs = getOrCalculateStatistic(currency.getSymbol(), fromInclusive, duration);
            if (ccs != null) statSortedSet.add(ccs);
        }
        return statSortedSet.stream().collect(Collectors.toList());
    }

    /**
     * Method CryptoCurrencyStatistic instance for  crypto with the highest normalized range for a specific day
     *
     * @param specificDay The day to calculate ranges and to choose crypto with highest range.
     * @return CryptoCurrencyStatistic instance
     */
    public CryptoCurrencyStatistic calculateHighestNormalizedRangeForDay(LocalDate specificDay) {
        LocalDateTime date = LocalDateTime.of(specificDay, LocalTime.MIN);
        List<CryptoCurrencyStatistic> statisticList = calculateDescendingCryptoCurrencyList(date, Duration.ofDays(1));
        if (statisticList == null || statisticList.isEmpty()) return null;
        return statisticList.get(0);

    }

    /**
     * Method calculates CryptoCurrencyStatistic for particular cryptocurrency for period in time
     *
     * @param currencyName  String The name of cryptocurrency (symbol, like 'BTC' etc.)
     * @param fromInclusive LocalDateTime for start period (inclusive)
     * @param duration      Duration for duration of period
     * @return CryptoCurrencyStatistic
     */
    public CryptoCurrencyStatistic calculateStatistic(String currencyName, LocalDateTime fromInclusive, Duration duration) {
        CryptoCurrency currency = currencyMap.get(currencyName);
        SortedSet<TimedValue> filteredValues = currency.getValues()
                .subSet((new TimedValue(fromInclusive, null)),
                        new TimedValue(fromInclusive.plus(duration), null));

        CryptoCurrencyStatistic statistic = new CryptoCurrencyStatistic();
        statistic.setSymbol(currency.getSymbol());
        statistic.setStart(fromInclusive);
        statistic.setDuration(duration);
        if (filteredValues.isEmpty()) return null;
        statistic.setOldest(Collections.max(filteredValues));
        statistic.setNewest(Collections.min(filteredValues));
        SortedSet<PricedValue> orderedByPriceTimedValues = new TreeSet<>();
        filteredValues.stream().map(t -> new PricedValue(t.getLocalDateTime(), t.getPrice()))
                .forEach(orderedByPriceTimedValues::add);
        PricedValue minPricedValue = Collections.min(orderedByPriceTimedValues);
        statistic.setMin(new TimedValue(minPricedValue.getLocalDateTime(), minPricedValue.getPrice()));
        PricedValue maxPricedValue = Collections.max(orderedByPriceTimedValues);
        statistic.setMax(new TimedValue(maxPricedValue.getLocalDateTime(), maxPricedValue.getPrice()));
        statistic.setNormalizedRange((statistic.getMax().getPrice() - statistic.getMin().getPrice())
                / statistic.getMin().getPrice());

        return statistic;
    }

    public boolean validateCryptoName(String cryptoName) {
        if (!currencyMap.containsKey(cryptoName))
            throw new CryptoNameValidationException("Provided crypto '" + cryptoName
                    + "' is not existed in our service");
        return true;
    }
}
