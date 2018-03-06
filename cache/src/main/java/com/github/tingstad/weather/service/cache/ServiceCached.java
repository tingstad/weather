package com.github.tingstad.weather.service.cache;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status;
import com.github.tingstad.weather.service.api.TimeProvider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *  ,d88b.d88b,
 *  88888888888  Decorator
 *  `Y8888888Y'  pattern
 *    `Y888Y'
 *      `Y'
 */
public class ServiceCached implements Service {

    private final Service origin;
    private final TimeProvider timeProvider;
    private final Map<LocalDateTime, Status> cache = new HashMap<>();

    public ServiceCached(Service origin, TimeProvider timeProvider) {
        this.origin = origin;
        this.timeProvider = timeProvider;
    }

    @Override
    public Status getStatus() {
        return cache.keySet().stream()
                .filter(time -> time.plusMinutes(5).isAfter(timeProvider.getTime()))
                .findFirst()
                .map(key -> cache.get(key))
                .orElseGet(() -> {
                    Status value = origin.getStatus();
                    cache.clear();
                    cache.put(LocalDateTime.now(), value);
                    return value;
                });
    }

}
