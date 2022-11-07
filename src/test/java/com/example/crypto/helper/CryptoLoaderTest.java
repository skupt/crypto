package com.example.crypto.helper;

import com.example.crypto.model.CryptoCurrency;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CryptoLoaderTest {
    private static Path projectRoot = Paths.get(System.getProperty("user.dir"));
    private static Path pricesDir = projectRoot.resolve("src/test/resources/prices");

    @Test
    public void shouldCreateSetHaving3CurrenciesEachHaving10values() throws IOException {
        Set<CryptoCurrency> currencySet = CryptoLoader.loadCryptoPrices(pricesDir.toString());
        assertNotNull(currencySet);
        assertEquals(3, currencySet.size());
        currencySet.forEach(s -> assertEquals(10, s.getValues().size()));
    }
}
