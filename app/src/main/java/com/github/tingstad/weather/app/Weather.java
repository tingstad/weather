package com.github.tingstad.weather.app;

import com.github.tingstad.weather.http.HttpClient;
import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.TimeProvider;
import com.github.tingstad.weather.service.cache.ServiceCached;
import com.github.tingstad.weather.service.yr.ServiceYr;

public class Weather implements WeatherInterface {

    private final Service yrService;

    public Weather(TimeProvider timeProvider) {
        yrService = new ServiceCached(
                new ServiceYr(
                        new HttpClient("http://www.yr.no/sted/Norge/Oslo/Oslo/Oslo/varsel.xml")
                ),
                timeProvider
        );
    }

    public String getContent() {
        String data = yrService.getText();
        return data;
    }

}
