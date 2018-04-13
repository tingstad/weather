package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;

public class SmsLimitedWeather implements WeatherInterface {

    private final WeatherInterface origin;
    private final TimeProvider timeProvider;

    public SmsLimitedWeather(WeatherInterface origin, TimeProvider timeProvider) {
        this.origin = origin;
        this.timeProvider = timeProvider;
    }

    @Override
    public StatusAll getStatus() {
        StatusAll status = origin.getStatus();
        return new StatusAll(
                status.getText()
                , status.getSeverity()
                , shouldSendSms(status)
        );
    }

    private boolean shouldSendSms(StatusAll status) {
        if (!status.shouldSendSms()) {
            return false;
        }
        Severity severity = status.getSeverity();
        if (severity.equals(Severity.HIGH)) {
            return true;
        }
        LocalDateTime time = timeProvider.getTime();
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        boolean isWorkDay = !EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(dayOfWeek);
        if (severity == Severity.MEDIUM && isWorkDay) {
            return true;
        }
        if (dayOfWeek == DayOfWeek.MONDAY) {
            return true;
        }
        return false;
    }

}
