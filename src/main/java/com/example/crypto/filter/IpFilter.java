package com.example.crypto.filter;

import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class helps to rate limit malicious users (based on IP)
 */
public class IpFilter implements Filter {
    private Map<String, Integer> rate = new HashMap<>();
    private Map<String, Long> lastReset = new HashMap<>();
    private int maxRate = 10;
    private long rateResetPeriod = 60 * 1000;

    public IpFilter() {
    }

    public IpFilter(int maxRate, long rateResetPeriod) {
        this.maxRate = maxRate;
        this.rateResetPeriod = rateResetPeriod;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * Filter prevent malicious user request attempts in the following algorithm:
     * It counts user requests and send 403 FORBIDDEN to user if count more than field value 'maxRate' of this filter
     * per duration in millis (field 'rateResetPeriod').
     *
     * @param servletRequest  ServletRequest
     * @param servletResponse ServletResponse
     * @param filterChain     FilterChain
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String ip = servletRequest.getRemoteAddr();
        rate.compute(ip, (k, v) -> (v == null) ? 1 : v + 1);
        if (rate.getOrDefault(ip, 0) >= maxRate) {
            long now = System.currentTimeMillis();
            Long lastResetMillis = lastReset.get(ip);
            if (lastResetMillis == null) {
                lastResetMillis = now - rateResetPeriod;
            }
            if ((now - lastResetMillis) >= rateResetPeriod) {
                rate.put(ip, 0);
                lastReset.put(ip, now);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.reset();
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.sendError(403, "Request limit exceeded: " + maxRate);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
