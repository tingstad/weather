package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.StatusAll;
import com.github.tingstad.weather.service.api.Status;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MainJobTest {

    private MainJob job = new MainJob();

    @Test
    public void highPriorityShouldSendSms() {
        boolean shouldSendSms = job.shouldSendSms(
                new StatusAll("", Status.Severity.HIGH, true));

        assertThat(shouldSendSms, is(true));
    }

}
