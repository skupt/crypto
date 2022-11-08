package com.example.crypto.helper;

import com.example.crypto.model.CryptoCurrency;
import com.example.crypto.model.TimedValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reads all the prices from the csv files
 */
public class CryptoLoader {

    public static Map<String, CryptoCurrency> loadCryptoPrices(String path) throws IOException {
        Map<String, CryptoCurrency> cryptoCurrencyMap = new HashMap<>();
        List<Path> filePathList = loadFileNames(path);
        for (Path filePath : filePathList) {
            CryptoCurrency cryptoCurrency = createCryptoCurrency(filePath);
            cryptoCurrencyMap.put(cryptoCurrency.getSymbol(), createCryptoCurrency(filePath));
        }

        return cryptoCurrencyMap;
    }

    private static List<Path> loadFileNames(String path) throws IOException {
        List<Path> filePaths = Files.list(Paths.get(path))
                .map(p -> p.toString())
                .filter(s -> s.endsWith("_values.csv"))
                .map(s -> Paths.get(s))
                .collect(Collectors.toList());

        return filePaths;
    }

    public static CryptoCurrency createCryptoCurrency(Path filePath) throws IOException {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        String fileName = filePath.getFileName().toString();
        int endOfSymbolIndex = fileName.lastIndexOf("_");
        String symbol = fileName.substring(0, endOfSymbolIndex);
        cryptoCurrency.setSymbol(symbol);
        Files.readAllLines(filePath).stream()
                .skip(1)
                .map(s -> {
                    return getTimedValue(s);
                })
                .forEach(v -> cryptoCurrency.getValues().add(v));

        return cryptoCurrency;
    }

    private static TimedValue getTimedValue(String s) {
        TimedValue value = new TimedValue();
        String[] parts = s.split(",");
        Double price = Double.parseDouble(parts[2]);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(parts[0])), ZoneId.systemDefault());
        value.setLocalDateTime(localDateTime);
        value.setPrice(price);
        return value;
    }

//    public static void main(String[] args) throws IOException {
//        // getTimedValue
//        String line = "1641009600000,BTC,46813.21";
//        TimedValue timedValue = CryptoLoader.getTimedValue(line);
//        System.out.println(timedValue.toString());
//
//        //
//        System.out.println("createCryptoCurrency");
//        String filePath = "C:\\Users\\Vitalii_Rozaronov\\Documents\\projects\\inerview-tasks\\crypto\\src\\test\\resources\\prices\\BTC_values.csv";
//        CryptoCurrency cc = createCryptoCurrency(Paths.get(filePath));
//        cc.getValues().forEach(System.out::println);
//
//        System.out.println(cc);
//
//        System.out.println("get Class path");
//        CryptoLoader cl = new CryptoLoader();
//        System.out.println(System.getProperty("user.dir"));
//
//    }
}
