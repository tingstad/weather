package com.github.tingstad.weather.app;

import com.github.tingstad.weather.sms.SmsService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;

public class MainJob {

    final private TimeProvider timeProvider;

    public static void main(String[] args) {
        new MainJob(new RealOsloTimeProvider())
                .work();
    }

    public MainJob(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;

    }

    public void work() {
        if (!shouldDoWork()) {
            return;
        }
        String content = new Weather().getContent();

        new SmsService().sendSms(content);
    }

    boolean shouldDoWork() {
        LocalDateTime time = timeProvider.getTime();
        return EnumSet.of(DayOfWeek.MONDAY).contains(time.getDayOfWeek());
    }

}
