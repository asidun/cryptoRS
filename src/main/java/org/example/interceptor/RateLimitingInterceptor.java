package org.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long ONE_MINUTE_IN_MILLIS = 60_000L;

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();

        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        requestTimestamps.putIfAbsent(clientIp, System.currentTimeMillis());

        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - requestTimestamps.get(clientIp);

        if (timeDifference > ONE_MINUTE_IN_MILLIS) {
            requestCounts.get(clientIp).set(0);
            requestTimestamps.put(clientIp, currentTime);
        } else {
            if (requestCounts.get(clientIp).incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }
        return true;
    }
}
