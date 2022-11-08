package com.example.crypto.controller;

import com.example.crypto.dto.CryptoCurrencyStatisticForAllTime;
import com.example.crypto.dto.CryptoRange;
import com.example.crypto.dto.ExceptionDto;
import com.example.crypto.exception.CryptoNameValidationException;
import com.example.crypto.exception.NotFoundException;
import com.example.crypto.exception.WrongParameterValueException;
import com.example.crypto.model.CryptoCurrencyStatistic;
import com.example.crypto.service.CryptoStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cryptos")
@Tag(name = "Cryptos' statistic", description = "Rest endpoints for providing crypto statistic info")
public class StatisticController {

    @Autowired
    private CryptoStatisticService statisticService;


    @GetMapping(value = "range", params = {"sort"})
    @Operation(summary = "Returns a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)")
    public List<CryptoRange> allCryptoForAllData(@Parameter(description = "sort order, possible value 'desc'")
                                                 @RequestParam("sort") String sort) {
        List<CryptoRange> descListCryptos = new ArrayList<>();
        if (sort.equals("desc")) {
            statisticService.calculateDescendingCryptoCurrencyList(LocalDateTime.of(2000, 1, 1, 0, 0),
                    Duration.ofDays(365000)).forEach(s -> {
                CryptoRange range = new CryptoRange();
                range.setCrypto(s.getSymbol());
                range.setRange(s.getNormalizedRange());
                descListCryptos.add(range);
            });
        } else {
            throw new WrongParameterValueException("Check values for request params 'sort'," +
                    " possible value is 'desc'");
        }
        return descListCryptos;
    }

    @GetMapping(value = "statistic", params = {"crypto"})
    @Operation(summary = "Returns the oldest/newest/min/max values for a requested crypto for all available data")
    public CryptoCurrencyStatisticForAllTime statisticForParticularCryptoForAllData(
            @Parameter(description = "crypto name (symbol)") @RequestParam("crypto") String crypto) {
        statisticService.validateCryptoName(crypto);
        CryptoCurrencyStatisticForAllTime cryptoCurrencyStatisticForAllTime = new CryptoCurrencyStatisticForAllTime();
        CryptoCurrencyStatistic currencyStatistic = statisticService.getOrCalculateStatisticForWholeTime(crypto);
        cryptoCurrencyStatisticForAllTime.setMax(currencyStatistic.getMax().getPrice());
        cryptoCurrencyStatisticForAllTime.setMin(currencyStatistic.getMin().getPrice());
        cryptoCurrencyStatisticForAllTime.setNewest(currencyStatistic.getNewest().getPrice());
        cryptoCurrencyStatisticForAllTime.setOldest(currencyStatistic.getOldest().getPrice());
        cryptoCurrencyStatisticForAllTime.setSymbol(currencyStatistic.getSymbol());
        return cryptoCurrencyStatisticForAllTime;
    }

    @GetMapping(value = "range/highest", params = {"date"})
    @Operation(summary = "Returns the crypto with the highest normalized range for a specific day")
    public CryptoRange highestRangeForSpecificDay(@Parameter(description = "string date in ISO format like YYYY-MM-DD")
                                                  @RequestParam("date") String date) {
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new WrongParameterValueException("Check value for date parameter." +
                    "It should be in ISO format like: YYYY-MM-DD");
        }
        CryptoRange cryptoRange = new CryptoRange();
        CryptoCurrencyStatistic currencyStatistic = statisticService.calculateHighestNormalizedRangeForDay(localDate);
        if (currencyStatistic == null)
            throw new NotFoundException("No data found for requested date: " + localDate);
        cryptoRange.setCrypto(currencyStatistic.getSymbol());
        cryptoRange.setRange(currencyStatistic.getNormalizedRange());
        return cryptoRange;
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({WrongParameterValueException.class, CryptoNameValidationException.class})
    public ExceptionDto return400(RuntimeException e) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ExceptionDto return404(RuntimeException e) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, e.getMessage());
    }


}
