package com.example.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A rest based recommendation service (Spring Boot application) to help developers choose cryptos for theirs salaries.
 */
@SpringBootApplication
public class CryptoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoApplication.class, args);
    }

}
