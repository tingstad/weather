module com.github.tingstad.weather.app {
    requires jdk.httpserver;
    requires com.github.tingstad.weather.service.api;
    uses com.github.tingstad.weather.service.api.Service;
}
