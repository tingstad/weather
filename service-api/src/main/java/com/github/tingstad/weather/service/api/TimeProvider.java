package com.github.tingstad.weather.service.api;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime getTime();

}
