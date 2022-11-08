package com.example.crypto.controller;

import com.example.crypto.dto.CryptoCurrencyStatisticForAllTime;
import com.example.crypto.dto.CryptoRange;
import com.example.crypto.dto.ExceptionDto;
import com.example.crypto.exception.CryptoNameValidationException;
import com.example.crypto.exception.NotFoundException;
import com.example.crypto.exception.WrongParameterValueException;
import com.example.crypto.model.CryptoCurrencyStatistic;
import com.example.crypto.service.CryptoStatisticService;
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
@RequestMapping("/api/v1")
public class StatisticController {

    @Autowired
    private CryptoStatisticService statisticService;

    /*
    @GetMapping(value = "/", params = "id")
    public String checkAccount(@RequestParam("id") long id, Model model) {
        BigDecimal amount = bookingWebAppFacade2.checkAccount(id);
        String msg = "Account of user: " + id + " has: " + amount + " money amount";
        model.addAttribute("msg", msg);
        return "inform";
    }
    */

    @GetMapping(value = "cryptos", params = {"param", "sort"})
    public List<CryptoRange> allCryptoForAllData(@RequestParam("param") String param, @RequestParam("sort") String sort) {
        List<CryptoRange> descListCryptos = new ArrayList<>();
        if (param.equals("range") && sort.equals("desc")) {
            statisticService.calculateDescendingCryptoCurrencyList(LocalDateTime.of(2000, 1, 1, 0, 0),
                    Duration.ofDays(365000)).forEach(s -> {
                CryptoRange range = new CryptoRange();
                range.setCrypto(s.getSymbol());
                range.setRange(s.getNormalizedRange());
                descListCryptos.add(range);
            });
        } else {
            throw new WrongParameterValueException("Check values for request params 'param' and 'sort'," +
                    " possible values are 'range' and 'desc' respectively");
        }
        return descListCryptos;
    }

    @GetMapping(value = "cryptos", params = {"param", "crypto"})
    public CryptoCurrencyStatisticForAllTime statisticForParticularCryptoForAllData(@RequestParam("param") String param, @RequestParam("crypto") String crypto) {
        statisticService.validateCryptoName(crypto);
        CryptoCurrencyStatisticForAllTime cryptoCurrencyStatisticForAllTime = null;
        if (param.equals("statistic")) {
            CryptoCurrencyStatistic currencyStatistic = statisticService.getOrCalculateStatisticForWholeTime(crypto);
            cryptoCurrencyStatisticForAllTime = new CryptoCurrencyStatisticForAllTime();
            cryptoCurrencyStatisticForAllTime.setMax(currencyStatistic.getMax().getPrice());
            cryptoCurrencyStatisticForAllTime.setMin(currencyStatistic.getMin().getPrice());
            cryptoCurrencyStatisticForAllTime.setNewest(currencyStatistic.getNewest().getPrice());
            cryptoCurrencyStatisticForAllTime.setOldest(currencyStatistic.getOldest().getPrice());
            cryptoCurrencyStatisticForAllTime.setSymbol(currencyStatistic.getSymbol());
        } else {
            throw new WrongParameterValueException("Check values for request params 'param'" +
                    " possible value is 'statistic'");
        }
        return cryptoCurrencyStatisticForAllTime;
    }

    //calculateHighestNormalizedRangeForDay(LocalDate specificDay)
    @GetMapping(value = "cryptos", params = {"param", "date"})
    public CryptoRange highestRangeForSpecificDay(@RequestParam("param") String param, @RequestParam("date") String date) {
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new WrongParameterValueException("Check value for date parameter." +
                    "It should be in ISO format like: YYYY-MM-DD");
        }
        CryptoRange cryptoRange = new CryptoRange();
        if (param.equals("highestRange")) {
            CryptoCurrencyStatistic currencyStatistic = statisticService.calculateHighestNormalizedRangeForDay(localDate);
            if (currencyStatistic == null)
                throw new NotFoundException("No data found for requested date: " + localDate);
            cryptoRange.setCrypto(currencyStatistic.getSymbol());
            cryptoRange.setRange(currencyStatistic.getNormalizedRange());
        } else {
            throw new WrongParameterValueException("Check values for request params 'param'" +
                    " possible value is 'highestRange'");
        }
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
