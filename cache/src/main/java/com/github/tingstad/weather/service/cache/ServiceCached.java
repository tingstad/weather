package com.github.tingstad.weather.service.cache;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.TimeProvider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ServiceCached implements Service {

    private final Service origin;
    private final TimeProvider timeProvider;
    private final Map<LocalDateTime, String> cache = new HashMap<>();

    public ServiceCached(Service origin, TimeProvider timeProvider) {
        this.origin = origin;
        this.timeProvider = timeProvider;
    }

    @Override
    public String getText() {
        return cache.keySet().stream()
                .filter(time -> time.plusMinutes(5).isAfter(timeProvider.getTime()))
                .findFirst()
                .map(key -> cache.get(key))
                .orElseGet(() -> {
                    String value = origin.getText();
                    cache.clear();
                    cache.put(LocalDateTime.now(), value);
                    return value;
                });
    }

}
