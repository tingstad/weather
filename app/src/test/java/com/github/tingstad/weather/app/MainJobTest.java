package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.Status;
import com.github.tingstad.weather.domain.Status.Priority;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MainJobTest {

    private MainJob job = new MainJob(() -> LocalDateTime.now());

    @Test
    public void highPriorityShouldSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new Status("", Priority.HIGH));

        assertThat(shouldSendSms, is(true));
    }

    @Test
    public void normalPriorityShouldSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new Status("", Priority.NORMAL));

        assertThat(shouldSendSms, is(true));
    }

    @Test
    public void lowPriorityShouldMotSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new Status("", Priority.LOW));

        assertThat(shouldSendSms, is(false));
    }

}
