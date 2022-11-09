package com.example.crypto;

import com.example.crypto.filter.IpFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * A rest based recommendation service (Spring Boot application) to help developers choose cryptos for theirs salaries.
 */
@SpringBootApplication
public class CryptoApplication {
    @Value("${max.attempts.ip}")
    String maxAttemptsIp = "10";
    @Value("${max.attempts.reset.millis}")
    String maxAttemptsResetMillis = "60000";

    @Bean
    public FilterRegistrationBean<IpFilter> loggingFilter() {
        FilterRegistrationBean<IpFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new IpFilter(Integer.parseInt(maxAttemptsIp), Long.parseLong(maxAttemptsResetMillis)));
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Integer.MAX_VALUE);

        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(CryptoApplication.class, args);
    }

}
