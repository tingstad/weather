package com.github.tingstad.weather.app;

import com.github.tingstad.weather.domain.StatusAll;
import com.github.tingstad.weather.sms.SmsService;

public class MainJob {

    public static void main(String[] args) {
        new MainJob().work();
        System.exit(0);
    }

    public void work() {
        StatusAll status = Composer.create().getStatus();
        String content = status.getText();
        if (shouldSendSms(status)) {
            new SmsService().sendSms(content);
        }
    }

    boolean shouldSendSms(StatusAll status) {
        return status.shouldSendSms();
    }

}
