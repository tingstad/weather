package com.github.tingstad.weather.service.api;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class RealOsloTimeProvider implements TimeProvider{

    @Override
    public LocalDateTime getTime() {
        return LocalDateTime.now(ZoneId.of("Europe/Oslo"));
    }

}
