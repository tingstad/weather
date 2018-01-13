package com.github.tingstad.weather.app;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.TimeProvider;
import com.github.tingstad.weather.service.yr.ServiceCached;
import com.github.tingstad.weather.service.yr.ServiceYr;

public class Weather implements WeatherInterface {

    private final Service yrService;

    public Weather(TimeProvider timeProvider) {
        yrService = new ServiceCached(new ServiceYr(), timeProvider);
    }

    public String getContent() {
        String data = yrService.getText();
        return data;
    }

}
