package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status;
import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;
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
    private final int timeoutMs;
    private final TimeProvider timeProvider;
    private final Service yrService;
    private final Service ruterService;

    public Weather(int timeoutMs, TimeProvider timeProvider, Service yrService, Service ruterService) {
        this.timeoutMs = timeoutMs;
        this.timeProvider = timeProvider;
        this.yrService = yrService;
        this.ruterService = ruterService;
    }

    Weather(TimeProvider timeProvider, Service yrService, Service ruterService) {
        this(3_000, timeProvider, yrService, ruterService);
    }

    @Override
    public StatusAll getStatus() {
        try {
            return getStatusInternal();
        } catch (Exception e) {
            logger.error("", e);
            return new StatusAll("Execution exception", Severity.HIGH);
        }
    }

    private StatusAll getStatusInternal() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<com.github.tingstad.weather.service.api.Status>> futures = executorService.invokeAll(asList(
                () -> yrService.getStatus(),
                () -> ruterService.getStatus())
                , timeoutMs, TimeUnit.MILLISECONDS);
        Status yr = getStatus(futures.get(0), "yr");
        Status ruter = getStatus(futures.get(1), "ruter");
        boolean ruterHasContent = ruter.getText().length() > 0;
        return new StatusAll(
                ruter.getText()
                        + (ruterHasContent ? "\n" : "")
                        + yr.getText()
                , getPriority(ruterHasContent, yr));
    }

    private static Status getStatus(Future<Status> future, String name) throws InterruptedException, ExecutionException {
        try {
            return future.get();
        } catch (Exception e) {
            logger.error(name, e);
            return Status.create("Error " + name, Severity.HIGH);
        }
    }

    private Severity getPriority(boolean ruterHasContent, Status yr) {
        LocalDateTime time = timeProvider.getTime();
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        boolean isWorkDay = !EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(dayOfWeek);
        if (ruterHasContent) {
            return isWorkDay ? Severity.HIGH : Severity.LOW;
        }
        if (yr.getSeverity().compareTo(Severity.MEDIUM) >= 0) {
            return isWorkDay ? convert(yr.getSeverity()) : Severity.LOW;
        }
        return EnumSet.of(DayOfWeek.MONDAY).contains(dayOfWeek)
                ? Severity.MEDIUM
                : Severity.LOW;
    }

    private static Severity convert(Severity severity) {
        switch (severity) {
            case LOW:
                return Severity.LOW;
            case MEDIUM:
                return Severity.MEDIUM;
        }
        return Severity.HIGH;
    }

}
