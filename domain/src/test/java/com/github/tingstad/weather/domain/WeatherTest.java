package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.domain.Status.Priority;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeatherTest {

    private TimeProvider timeProvider = () -> LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

    @Test
    public void onlyYr() {
        Weather weather = new Weather(timeProvider, () -> "yr", () -> "");

        Status status = weather.getStatus();

        assertThat(status.getPriority(), is(Priority.LOW));
        assertThat(status.getText(), is("yr"));
    }

    @Test
    public void ruterSituationShouldMakeStatusCritical() {
        Weather weather = new Weather(timeProvider, () -> "yr", () -> "problem");

        Status status = weather.getStatus();

        assertThat(status.getPriority(), is(Priority.HIGH));
    }

    @Test
    public void yrAndRuterTextsShouldBeConcatinated() {
        Weather weather = new Weather(timeProvider, () -> "yr", () -> "problem");

        Status status = weather.getStatus();

        assertThat(status.getText(), is("problem\nyr"));
    }

    @Test
    public void bothYrAndRuterShouldBeCalled() {
        AtomicInteger yr = new AtomicInteger(0);
        AtomicInteger ruter = new AtomicInteger(0);
        Weather weather = new Weather(timeProvider,
                () -> "" + yr.incrementAndGet(),
                () -> "" + ruter.incrementAndGet());

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
                        try {
                            i.wait(1_000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return "y" + i.incrementAndGet();
                },
                () -> {
                    int c;
                    synchronized (i) {
                        c = i.incrementAndGet();
                        i.notify();
                    }
                    return "r" + c;
                });

        Status status = weather.getStatus();

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
                    return "y" + c;
                },
                () -> {
                    synchronized (i) {
                        try {
                            i.wait(1_000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return "r" + i.incrementAndGet();
                });

        Status status = weather.getStatus();

        assertThat(status.getText(), is("r2\ny1"));
    }

    @Test
    public void yrExceptionShouldReturnErrorString() {
        Weather weather = new Weather(timeProvider,
                () -> { throw new RuntimeException("yr"); },
                () -> "ruter");

        Status status = weather.getStatus();

        assertThat(status.getText(), is("ruter\nError yr"));
    }

    @Test
    public void ruterExceptionShouldReturnErrorString() {
        Weather weather = new Weather(timeProvider,
                () -> "yr",
                () -> { throw new RuntimeException("yr"); });

        Status status = weather.getStatus();

        assertThat(status.getText(), is("Error ruter\nyr"));
    }

    @Test
    public void yrExceptionShouldReturnCriticalStatus() {
        Weather weather = new Weather(timeProvider,
                () -> { throw new RuntimeException("yr"); },
                () -> "");

        Status status = weather.getStatus();

        assertThat(status.getPriority(), is(Priority.HIGH));
    }

    @Test
    public void timeoutShouldReturnErrorString() {
        Weather weather = new Weather(1, timeProvider,
                () -> {
                    try {
                        Thread.sleep(1_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "yr"; },
                () -> "");

        Status status = weather.getStatus();

        assertThat(status.getPriority(), is(Priority.HIGH));
        assertThat(status.getText(), is("Error yr"));
    }

    @Test
    public void noProblemsShouldGiveNormalPriorityOnMondays() {
        LocalDateTime monday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeProvider timeProvider = () -> monday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> "yr", () -> "");

        assertThat(weather.getStatus().getPriority(), is(Priority.NORMAL));
    }

    @Test
    public void noProblemsShouldGiveLowPriorityOnWednesdays() {
        LocalDateTime wednesday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        TimeProvider timeProvider = () -> wednesday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> "yr", () -> "");

        assertThat(weather.getStatus().getPriority(), is(Priority.LOW));
    }

    @Test
    public void problemsShouldGiveHighPriorityOnWednesdays() {
        LocalDateTime wednesday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        TimeProvider timeProvider = () -> wednesday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> "yr", () -> "ruter: problem");

        assertThat(weather.getStatus().getPriority(), is(Priority.HIGH));
    }

    @Test
    public void ruterProblemsShouldGiveLowPriorityOnSundays() {
        LocalDateTime sunday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        TimeProvider timeProvider = () -> sunday;

        Weather weather = new Weather(1_000, timeProvider,
                () -> "yr", () -> "ruter: problem");

        assertThat(weather.getStatus().getPriority(), is(Priority.LOW));
    }

}

