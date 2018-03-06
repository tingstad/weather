package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.StatusAll;
import com.github.tingstad.weather.domain.StatusAll.Priority;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MainJobTest {

    private MainJob job = new MainJob();

    @Test
    public void highPriorityShouldSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new StatusAll("", Priority.HIGH));

        assertThat(shouldSendSms, is(true));
    }

    @Test
    public void normalPriorityShouldSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new StatusAll("", Priority.NORMAL));

        assertThat(shouldSendSms, is(true));
    }

    @Test
    public void lowPriorityShouldMotSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new StatusAll("", Priority.LOW));

        assertThat(shouldSendSms, is(false));
    }

}
