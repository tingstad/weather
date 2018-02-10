module com.github.tingstad.weather.app {
    requires jdk.httpserver;
    requires com.github.tingstad.weather.service.api;
    requires com.github.tingstad.weather.sms.service;
    requires com.github.tingstad.weather.service.cache;
    requires com.github.tingstad.weather.http;
    requires com.github.tingstad.weather.service.yr;
    requires slf4j.api;
}
