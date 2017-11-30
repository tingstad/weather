package com.github.tingstad.weather.app;

import com.github.tingstad.weather.service.api.Service;

import java.util.ServiceLoader;

public class Weather {

    public Weather() {
    }

    public String getContent() {
        ServiceLoader<Service> loader = ServiceLoader.load(Service.class);
        String data = loader.findFirst()
                .map(Service::getText)
                .orElse("No service");
        return data;
    }

}
