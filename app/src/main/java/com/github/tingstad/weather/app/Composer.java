package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.SmsLimitedWeather;
import com.github.tingstad.weather.domain.Weather;
import com.github.tingstad.weather.domain.WeatherInterface;
import com.github.tingstad.weather.http.HttpClient;
import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.TimeProvider;
import com.github.tingstad.weather.service.cache.ServiceCached;
import com.github.tingstad.weather.service.ruter.ServiceRuter;
import com.github.tingstad.weather.service.yr.ServiceYr;

/**
 * ♥ Composition Root
 * ♥ Pure Dependency Injection
 */
public class Composer {

    public static WeatherInterface create() {
        TimeProvider timeProvider = new RealOsloTimeProvider();
        Service yrService = new ServiceCached(
                new ServiceYr(
                        new HttpClient("http://www.yr.no/sted/Norge/Oslo/Oslo/Oslo/varsel.xml")
                ),
                timeProvider
        );
        Service ruterService = new ServiceCached(
                new ServiceRuter(
                        new HttpClient("http://sirisx.ruter.no/sx/situations/")
                                .header("Accept", "application/xml"),
                        timeProvider,
                        3
                ),
                timeProvider
        );
        return new SmsLimitedWeather(
                new Weather(30_000, timeProvider, yrService, ruterService)
                , timeProvider
        );
    }

}
