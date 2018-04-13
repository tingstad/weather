package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SmsLimitedWeatherTest {

    @Test
    public void shouldNotUpgradeDisabledSms() {
        assertFalse(
                shouldSendSms(noSms(Severity.HIGH), day(DayOfWeek.MONDAY))
        );
    }

    @Test
    public void shouldSendHighSeverityOnAnyDay() {
        assertTrue(
                shouldSendSms(sms(Severity.HIGH), day(DayOfWeek.SUNDAY))
        );
    }

    @Test
    public void shouldSendMediumSeverityOnAnyWorkday() {
        assertTrue(
                shouldSendSms(sms(Severity.MEDIUM), day(DayOfWeek.THURSDAY))
        );
    }

    @Test
    public void shouldSendLowSeverityOnMondays() {
        assertTrue(
                shouldSendSms(sms(Severity.LOW), day(DayOfWeek.MONDAY))
        );
    }

    private static boolean shouldSendSms(StatusAll status, TimeProvider timeProvider) {
        return new SmsLimitedWeather(weather(status), timeProvider).getStatus()
                .shouldSendSms();
    }

    private static WeatherInterface weather(StatusAll status) {
        return () -> status;
    }

    private static TimeProvider day(DayOfWeek dayOfWeek) {
        return () -> LocalDateTime.now()
                .with(TemporalAdjusters.next(dayOfWeek));
    }

    private static StatusAll sms(Severity severity) {
        return status(severity, true);
    }

    private static StatusAll noSms(Severity severity) {
        return status(severity, false);
    }

    private static StatusAll status(Severity severity, boolean shouldSendSms) {
        return new StatusAll("", severity, shouldSendSms);
    }

}
