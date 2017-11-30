package com.github.tingstad.weather.app;

import com.github.tingstad.weather.sms.SmsService;

public class MainJob {

    public static void main(String[] args) {
        String content = new Weather().getContent();

        new SmsService().sendSms(content);
    }
}
