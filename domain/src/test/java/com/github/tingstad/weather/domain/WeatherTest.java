package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tingstad.weather.service.api.Status.Severity.LOW;
import static com.github.tingstad.weather.service.api.Status.Severity.MEDIUM;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeatherTest {

    private TimeProvider timeProvider = () -> LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

    @Test
    public void onlyYr() {
        Weather weather = new Weather(timeProvider, () -> status("yr", LOW), () -> status(LOW));

        StatusAll status = weather.getStatus();

        assertThat(status.getSeverity(), is(Severity.LOW));
        assertThat(status.getText(), is("yr"));
    }

    @Test
    public void ruterSituationShouldMakeStatusCritical() {
        Weather weather = new Weather(timeProvider, () -> status(LOW), () -> status("problem", MEDIUM));

        StatusAll status = weather.getStatus();

        assertThat(status.getSeverity(), is(Severity.HIGH));
    }

    @Test
    public void yrAndRuterTextsShouldBeConcatinated() {
        Weather weather = new Weather(timeProvider, () -> status("yr"), () -> status("problem"));

        StatusAll status = weather.getStatus();

        assertThat(status.getText(), is("problem\nyr"));
    }

    @Test
    public void bothYrAndRuterShouldBeCalled() {
        AtomicInteger yr = new AtomicInteger(0);
        AtomicInteger ruter = new AtomicInteger(0);
        Weather weather = new Weather(timeProvider,
                () -> status("" + yr.incrementAndGet()),
                () -> status("" + ruter.incrementAndGet()));

        weather.getStatus();

        assertThat(yr.get(), is(1));
        assertThat(ruter.get(), is(1));
    }

    @Test
    public void shouldCompleteRuterFirstIfYrIsSlow() {
        AtomicInteger i = new AtomicInteger(0);
        Weather weather = new Weather(timeProvider,
                () -> {
                    synchronized (i) {
                        if (i.get() == 0) {
                            try {
                                i.wait(1_000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return status("y" + i.incrementAndGet());
                },
                () -> {
                    int c;
                    synchronized (i) {
                        c = i.incrementAndGet();
                        i.notify();
                    }
                    return status("r" + c);
                });

        StatusAll status = weather.getStatus();

        assertThat(status.getText(), is("r1\ny2"));
    }

    @Test
    public void shouldCompleteYrFirstIfRuterIsSlow() {
        AtomicInteger i = new AtomicInteger(0);
        Weather weather = new Weather(timeProvider,
                () -> {
                    int c;
                    synchronized (i) {
                        c = i.incrementAndGet();
                        i.notify();
                    }
                    return status("y" + c);
                },
                () -> {
                    synchronized (i) {
                        if (i.get() == 0) {
                            try {
                                i.wait(1_000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return status("r" + i.incrementAndGet());
                });

        StatusAll status = weather.getStatus();

        assertThat(status.getText(), is("r2\ny1"));
    }

    @Test
    public void yrExceptionShouldReturnErrorString() {
        Weather weather = new Weather(timeProvider,
                () -> { throw new RuntimeException("yr"); },
                () -> status("ruter"));

        StatusAll status = weather.getStatus();

        assertThat(status.getText(), is("ruter\nError yr"));
    }

    @Test
    public void ruterExceptionShouldReturnErrorString() {
        Weather weather = new Weather(timeProvider,
                () -> status("yr"),
                () -> { throw new RuntimeException("yr"); });

        StatusAll status = weather.getStatus();

        assertThat(status.getText(), is("Error ruter\nyr"));
    }

    @Test
    public void yrExceptionShouldReturnCriticalStatus() {
        Weather weather = new Weather(timeProvider,
                () -> { throw new RuntimeException("yr"); },
                () -> status(""));

        StatusAll status = weather.getStatus();

        assertThat(status.getSeverity(), is(Severity.HIGH));
    }

    @Test
    public void timeoutShouldReturnErrorString() {
        Weather weather = new Weather(100, timeProvider,
                () -> {
                    try {
                        Thread.sleep(1_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return status("yr"); },
                () -> status(""));

        StatusAll status = weather.getStatus();

        assertThat(status.getSeverity(), is(Severity.HIGH));
        assertThat(status.getText(), is("Error yr"));
    }

    @Test
    public void timeoutsShouldReturnErrorStrings() {
        Service timeout = () -> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return status("foo");
        };
        Weather weather = new Weather(1, timeProvider,
                timeout, timeout);

        StatusAll status = weather.getStatus();

        assertThat(status.getSeverity(), is(Severity.HIGH));
        assertThat(status.getText(), is("Error ruter\nError yr"));
    }

    @Test
    public void noProblemsShouldGiveNormalSeverityOnMondays() {
        LocalDateTime monday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeProvider timeProvider = () -> monday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> status("yr"), () -> status(""));

        assertThat(weather.getStatus().getSeverity(), is(Severity.MEDIUM));
    }

    @Test
    public void noProblemsShouldGiveLowSeverityOnWednesdays() {
        LocalDateTime wednesday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        TimeProvider timeProvider = () -> wednesday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> status("yr"), () -> status(""));

        assertThat(weather.getStatus().getSeverity(), is(Severity.LOW));
    }

    @Test
    public void problemsShouldGiveHighSeverityOnWednesdays() {
        LocalDateTime wednesday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        TimeProvider timeProvider = () -> wednesday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> status("yr"), () -> status("ruter: problem"));

        assertThat(weather.getStatus().getSeverity(), is(Severity.HIGH));
    }

    @Test
    public void ruterProblemsShouldGiveLowSeverityOnSundays() {
        LocalDateTime sunday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        TimeProvider timeProvider = () -> sunday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> status("yr"), () -> status("ruter: problem"));

        assertThat(weather.getStatus().getSeverity(), is(Severity.LOW));
    }

    private static com.github.tingstad.weather.service.api.Status status(Severity severity) {
        return com.github.tingstad.weather.service.api.Status.create("", severity);
    }

    private static com.github.tingstad.weather.service.api.Status status(String text) {
        return com.github.tingstad.weather.service.api.Status.create(text, Severity.LOW);
    }

    private static com.github.tingstad.weather.service.api.Status status(String text, Severity severity) {
        return com.github.tingstad.weather.service.api.Status.create(text, severity);
    }

}

