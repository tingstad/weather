package com.github.tingstad.weather.app;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeatherTest {

    @Test
    public void onlyYr() {
        Weather weather = new Weather(() -> "yr", () -> "");

        Status status = weather.getStatus();

        assertThat(status.isCritical(), is(false));
        assertThat(status.getText(), is("yr"));
    }

    @Test
    public void ruterSituationShouldMakeStatusCritical() {
        Weather weather = new Weather(() -> "yr", () -> "problem");

        Status status = weather.getStatus();

        assertThat(status.isCritical(), is(true));
    }

    @Test
    public void yrAndRuterTextsShouldBeConcatinated() {
        Weather weather = new Weather(() -> "yr", () -> "problem");

        Status status = weather.getStatus();

        assertThat(status.getText(), is("problem\nyr"));
    }

    @Test
    public void bothYrAndRuterShouldBeCalled() {
        AtomicInteger yr = new AtomicInteger(0);
        AtomicInteger ruter = new AtomicInteger(0);
        Weather weather = new Weather(
                () -> "" + yr.incrementAndGet(),
                () -> "" + ruter.incrementAndGet());

        weather.getStatus();

        assertThat(yr.get(), is(1));
        assertThat(ruter.get(), is(1));
    }

    @Test
    public void shouldCompleteRuterFirstIfYrIsSlow() throws Exception {
        AtomicInteger i = new AtomicInteger(0);
        Weather weather = new Weather(
                () -> {
                    synchronized (i) {
                        try {
                            i.wait(1000);
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
    public void shouldCompleteYrFirstIfRuterIsSlow() throws Exception {
        AtomicInteger i = new AtomicInteger(0);
        Weather weather = new Weather(
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
                            i.wait(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return "r" + i.incrementAndGet();
                });

        Status status = weather.getStatus();

        assertThat(status.getText(), is("r2\ny1"));
    }

}
