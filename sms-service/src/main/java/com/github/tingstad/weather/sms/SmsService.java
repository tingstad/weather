package com.github.tingstad.weather.sms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SmsService {

    private final String url;

    public SmsService() {
        this("https://maker.ifttt.com/trigger/weather/with/key/"
                + System.getenv("IFTTT"));
    }

    SmsService(String url) {
        this.url = url;
    }

    public void sendSms(String content) {
        try {
            sendSmsInternal(String.format("{ \"value1\" : \"%s\" }", content));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSmsInternal(String content) throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(1_000);
            httpConnection.setReadTimeout(1_000);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setDoOutput(true);
            try (
                    OutputStream outputStream = httpConnection.getOutputStream();
            ) {
                outputStream.write(
                        content.getBytes("UTF-8"));
                outputStream.flush();
            }
            try (
                    InputStream inputStream = httpConnection.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            ) {
                String response = scanner.next();
                int responseCode = httpConnection.getResponseCode();
                if (responseCode >= 400) {
                    throw new RuntimeException("Unexpected response code: " + responseCode + " " + response);
                }
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

}