package com.example.crypto.helper;

import com.example.crypto.model.CryptoCurrency;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CryptoLoaderTest {
    private static Path projectRoot = Paths.get(System.getProperty("user.dir"));
    private static Path pricesDir = projectRoot.resolve("src/test/resources/prices");

    @Test
    public void shouldCreateMapHaving3CurrenciesEachHaving10values() throws IOException {
        Map<String, CryptoCurrency> currencyMap = CryptoLoader.loadCryptoPrices(pricesDir.toString());
        assertNotNull(currencyMap);
        assertEquals(3, currencyMap.size());
        currencyMap.entrySet().forEach(s -> assertEquals(10, s.getValue().getValues().size()));
    }
}
