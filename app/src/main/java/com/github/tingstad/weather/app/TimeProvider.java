package com.github.tingstad.weather.app;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime getTime();

}
