module com.github.tingstad.weather.service.yr {
    exports com.github.tingstad.weather.service.yr;
    requires com.github.tingstad.weather.service.api;
    requires java.xml;
    provides com.github.tingstad.weather.service.api.Service
            with com.github.tingstad.weather.service.yr.ServiceYr;
}
