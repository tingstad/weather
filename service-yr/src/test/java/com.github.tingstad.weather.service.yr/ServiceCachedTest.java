package com.github.tingstad.weather.service.yr;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ServiceCachedTest {

    @Test
    public void shouldCallServiceWhenEmptyCache() {
        Service service = () -> "value";
        TimeProvider timeProvider = () -> LocalDateTime.now();
        ServiceCached serviceCached = new ServiceCached(service, timeProvider);

        assertThat(serviceCached.getText(), is("value"));
    }

    @Test
    public void twoFastCallsShouldCallServiceOnlyOnce() {
        AtomicInteger count = new AtomicInteger(0);
        Service service = () -> {
            count.incrementAndGet();
            return "value";
        };
        TimeProvider timeProvider = () -> LocalDateTime.now();
        ServiceCached serviceCached = new ServiceCached(service, timeProvider);
        serviceCached.getText();

        assertThat(serviceCached.getText(), is("value"));
        assertThat(count.get(), is(1));
    }

    @Test
    public void shouldCallServiceWhenCacheIsOld() {
        AtomicInteger count = new AtomicInteger(0);
        Service service = () -> {
            count.incrementAndGet();
            return "value";
        };
        TimeProvider timeProvider = () -> LocalDateTime.now().plusHours(count.get());
        ServiceCached serviceCached = new ServiceCached(service, timeProvider);
        serviceCached.getText();

        assertThat(serviceCached.getText(), is("value"));
        assertThat(count.get(), is(2));
    }

}
