package com.github.tingstad.weather.app;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MainJobTest {

    @Test
    public void shouldRunOnMondays() {
        LocalDateTime monday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeProvider timeProvider = () -> monday;

        MainJob job = new MainJob(timeProvider);

        assertThat(job.shouldDoWork(), is(true));
    }

    @Test
    public void shouldNotRunOnWednesdays() {
        LocalDateTime monday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        TimeProvider timeProvider = () -> monday;

        MainJob job = new MainJob(timeProvider);

        assertThat(job.shouldDoWork(), is(false));
    }

}
