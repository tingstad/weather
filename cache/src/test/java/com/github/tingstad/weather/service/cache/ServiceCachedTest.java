package com.github.tingstad.weather.service.cache;

import com.github.tingstad.weather.service.api.Service;
import com.github.tingstad.weather.service.api.Status;
import com.github.tingstad.weather.service.api.Status.Severity;
import com.github.tingstad.weather.service.api.TimeProvider;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ServiceCachedTest {

    @Test
    public void shouldCallServiceWhenEmptyCache() {
        Service service = () -> status("value");
        TimeProvider timeProvider = () -> LocalDateTime.now();
        ServiceCached serviceCached = new ServiceCached(service, timeProvider);

        assertThat(serviceCached.getStatus().getText(), is("value"));
    }

    @Test
    public void twoFastCallsShouldCallServiceOnlyOnce() {
        AtomicInteger count = new AtomicInteger(0);
        Service service = () -> {
            count.incrementAndGet();
            return status("value");
        };
        TimeProvider timeProvider = () -> LocalDateTime.now();
        ServiceCached serviceCached = new ServiceCached(service, timeProvider);
        serviceCached.getStatus().getText();

        assertThat(serviceCached.getStatus().getText(), is("value"));
        assertThat(count.get(), is(1));
    }

    @Test
    public void shouldCallServiceWhenCacheIsOld() {
        AtomicInteger count = new AtomicInteger(0);
        Service service = () -> {
            count.incrementAndGet();
            return status("value");
        };
        TimeProvider timeProvider = () -> LocalDateTime.now().plusHours(count.get());
        ServiceCached serviceCached = new ServiceCached(service, timeProvider);
        serviceCached.getStatus().getText();

        assertThat(serviceCached.getStatus().getText(), is("value"));
        assertThat(count.get(), is(2));
    }

    private static com.github.tingstad.weather.service.api.Status status(String text) {
        return com.github.tingstad.weather.service.api.Status.create(text, Severity.LOW);
    }

}
