package com.example.crypto.controller;

import com.example.crypto.dto.CryptoCurrencyStatisticForAllTime;
import com.example.crypto.dto.CryptoRange;
import com.example.crypto.exception.CryptoNameValidationException;
import com.example.crypto.exception.NotFoundException;
import com.example.crypto.exception.WrongParameterValueException;
import com.example.crypto.model.CryptoCurrencyStatistic;
import com.example.crypto.model.TimedValue;
import com.example.crypto.service.CryptoStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class StatisticControllerTest {
    private StatisticController controller;

    @Mock
    private CryptoStatisticService service;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        controller = new StatisticController(service);
    }

    @Test
    public void givenSortDescWhenCallAllCryptoForAllDataThenReturnListOfCryptoRange() {
        List<CryptoCurrencyStatistic> statisticList = new ArrayList<>();
        CryptoCurrencyStatistic statistic = getStatistic();
        statisticList.add(statistic);

        given(service.calculateDescendingCryptoCurrencyList(Mockito.any(LocalDateTime.class), Mockito.any(Duration.class)))
                .willReturn(statisticList);

        List<CryptoRange> cryptoRangeList = controller.allCryptoForAllData("desc");

        assertNotNull(cryptoRangeList);
        assertEquals("BTC", statistic.getSymbol());
        assertEquals(0.5, statistic.getNormalizedRange());
    }

    @Test
    public void givenSortNotDescWhenCallAllCryptoForAllDataThenThrowWrongParameterValueException() {
        assertThrows(WrongParameterValueException.class, () -> {
            controller.allCryptoForAllData("NotDesc");
        });
    }

    @Test
    public void givenSymbolWhenCallStatisticForParticularCryptoForAllDataThenReturnCryptoCurrencyStatisticForAllData() {
        CryptoCurrencyStatistic statistic = getStatistic();
        given(service.validateCryptoName(Mockito.anyString())).willReturn(true);
        given(service.getOrCalculateStatisticForWholeTime(Mockito.anyString())).willReturn(statistic);

        CryptoCurrencyStatisticForAllTime expected = new CryptoCurrencyStatisticForAllTime();
        expected.setSymbol(statistic.getSymbol());
        expected.setOldest(statistic.getOldest().getPrice());
        expected.setNewest(statistic.getNewest().getPrice());
        expected.setMin(statistic.getMin().getPrice());
        expected.setMax(statistic.getMax().getPrice());
        CryptoCurrencyStatisticForAllTime actual = controller.statisticForParticularCryptoForAllData("BTC");

        assertEquals(expected, actual);
    }

    @Test
    public void givenWrongSymbolWhenCallStatisticForParticularCryptoForAllDataThenThrowCryptoNameValidationException() {
        given(service.validateCryptoName(Mockito.anyString())).willThrow(new CryptoNameValidationException());

        assertThrows(CryptoNameValidationException.class, () -> {
            controller.statisticForParticularCryptoForAllData("WrongCryptoName");
        });
    }

    @Test
    public void givenDateISOHavingDataWhenCallHighestRangeForSpecificDayThenReturnCryptoRange() {
        CryptoCurrencyStatistic statistic = getStatistic();
        given(service.calculateHighestNormalizedRangeForDay(Mockito.any(LocalDate.class))).willReturn(statistic);
        CryptoRange expected = new CryptoRange();
        expected.setRange(statistic.getNormalizedRange());
        expected.setCrypto(statistic.getSymbol());

        assertEquals(expected, controller.highestRangeForSpecificDay("2022-01-01"));
    }

    @Test
    public void givenWrongFormattedDateWhenCallHighestRangeForSpecificDayThenThrowsWrongParamValueException() {
        assertThrows(WrongParameterValueException.class, () -> {
            controller.highestRangeForSpecificDay("2022-1-1");
        });
    }

    @Test
    public void givenDateISONotHavingDataWhenCallHighestRangeForSpecificDayThenThrowsNotFoundException() {
        given(service.calculateHighestNormalizedRangeForDay(Mockito.any(LocalDate.class))).willReturn(null);

        assertThrows(NotFoundException.class, () -> {
            controller.highestRangeForSpecificDay("2022-01-01");
        });
    }


    private static CryptoCurrencyStatistic getStatistic() {
        CryptoCurrencyStatistic statistic = new CryptoCurrencyStatistic();
        statistic.setSymbol("BTC");
        statistic.setDuration(Duration.ofDays(31));
        statistic.setMin(new TimedValue(LocalDateTime.of(2022, 1, 1, 0, 0), 2500.0));
        statistic.setMax(new TimedValue(LocalDateTime.of(2022, 1, 15, 0, 0), 3500.0));
        statistic.setOldest(new TimedValue(LocalDateTime.of(2022, 1, 29, 0, 0), 3550.0));
        statistic.setNewest(new TimedValue(LocalDateTime.of(2022, 1, 1, 0, 0), 2550.0));
        statistic.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        statistic.setNormalizedRange(0.5);

        return statistic;
    }
}
