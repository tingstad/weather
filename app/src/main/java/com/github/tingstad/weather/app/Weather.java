package com.github.tingstad.weather.app;

import com.github.tingstad.weather.service.api.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class Weather implements WeatherInterface {

    private final static Logger logger = LoggerFactory.getLogger(Weather.class);
    private final Service yrService;
    private final Service ruterService;

    public Weather(Service yrService, Service ruterService) {
        this.yrService = yrService;
        this.ruterService = ruterService;
    }

    @Override
    public Status getStatus() {
        try {
            return getStatusInternal();
        } catch (Exception e) {
            logger.error("", e);
            return new Status("Execution exception", true);
        }
    }

    private Status getStatusInternal() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = executorService.invokeAll(asList(
                () -> yrService.getText(),
                () -> ruterService.getText())
                , 30, TimeUnit.SECONDS);
        String yr = futures.get(0).get();
        String ruter = futures.get(1).get();
        boolean ruterHasContent = ruter.length() > 0;
        return new Status(
                ruter
                        + (ruterHasContent ? "\n" : "")
                        + yr
                , ruterHasContent);
    }

}
