module com.github.tingstad.weather.app {
    requires jdk.httpserver;
    requires com.github.tingstad.weather.service.api;
    requires com.github.tingstad.weather.sms.service;
    uses com.github.tingstad.weather.service.api.Service;
}
