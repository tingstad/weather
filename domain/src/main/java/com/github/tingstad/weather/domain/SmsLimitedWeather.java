package com.github.tingstad.weather.domain;

import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;

public class SmsLimitedWeather implements WeatherInterface {

    private final static Logger logger = LoggerFactory.getLogger(SmsLimitedWeather.class);
    private final WeatherInterface origin;
    private final TimeProvider timeProvider;

    public SmsLimitedWeather(WeatherInterface origin, TimeProvider timeProvider) {
        this.origin = origin;
        this.timeProvider = timeProvider;
    }

    @Override
    public StatusAll getStatus() {
        StatusAll status = origin.getStatus();
        StatusAll result = new StatusAll(
                status.getText()
                , status.getSeverity()
                , shouldSendSms(status)
        );
        logger.info(result.toString());
        return result;
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
