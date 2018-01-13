module com.github.tingstad.weather.app {
    requires jdk.httpserver;
    requires com.github.tingstad.weather.service.api;
    requires com.github.tingstad.weather.sms.service;
    requires com.github.tingstad.weather.service.yr;
}
