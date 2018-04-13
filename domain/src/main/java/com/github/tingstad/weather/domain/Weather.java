package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status;
import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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
        StatusAll status;
        try {
            status = getStatusInternal();
        } catch (Exception e) {
            logger.error("", e);
            status = new StatusAll("Execution exception", Severity.HIGH, true);
        }
        logger.info(status.toString());
        return status;
    }

    private StatusAll getStatusInternal() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Status>> futures = executorService.invokeAll(asList(
                () -> yrService.getStatus(),
                () -> ruterService.getStatus())
                , timeoutMs, TimeUnit.MILLISECONDS);
        Status yr = getStatus(futures.get(0), "yr");
        Status ruter = getStatus(futures.get(1), "ruter");
        String ruterText = ruter.getText();
        Severity severity = Collections.max(EnumSet.of(yr.getSeverity(), ruter.getSeverity()));
        return new StatusAll(
                ruterText
                        + (ruterText.isEmpty() ? "" : "\n")
                        + yr.getText()
                , severity
                , shouldSendSms(ruter, yr));
    }

    private static Status getStatus(Future<Status> future, String name) {
        try {
            return future.get();
        } catch (Exception e) {
            logger.error(name, e);
            return Status.create("Error " + name, Severity.HIGH);
        }
    }

    private boolean shouldSendSms(Status ruter, Status yr) {
        Severity severity = Collections.max(EnumSet.of(yr.getSeverity(), ruter.getSeverity()));
        if (severity == Severity.HIGH) {
            return true;
        }
        LocalDateTime time = timeProvider.getTime();
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        boolean isWorkDay = !EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(dayOfWeek);
        return isWorkDay;
    }

}
