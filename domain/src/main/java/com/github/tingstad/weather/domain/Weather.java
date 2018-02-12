package com.github.tingstad.weather.domain;

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

/**
 * ♥ Dependency Inversion ♥
 */
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
        Status yr = getStatus(futures.get(0), "yr");
        Status ruter = getStatus(futures.get(1), "ruter");
        boolean ruterHasContent = ruter.getText().length() > 0;
        boolean critical = ruterHasContent || yr.isCritical();
        return new Status(
                ruter.getText()
                        + (ruterHasContent ? "\n" : "")
                        + yr.getText()
                , critical);
    }

    private static Status getStatus(Future<String> future, String name) throws InterruptedException, ExecutionException {
        try {
            return new Status(future.get(), false);
        } catch (Exception e) {
            logger.error(name, e);
            return new Status("Error " + name, true);
        }
    }

}
