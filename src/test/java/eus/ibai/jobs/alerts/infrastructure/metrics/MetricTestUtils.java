package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.Status;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.actuate.health.Status.UP;

public class MetricTestUtils {

    public static void verifyComponentHealthMetricRecorded(MeterRegistry meterRegistry, String componentName, Status status) {
        await().atMost(3, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            Gauge componentHealthGauge = meterRegistry.find("health.component")
                    .tag("component", componentName)
                    .tag("status", status.getCode())
                    .gauge();
            assertThat(componentHealthGauge, notNullValue());
            assertThat(componentHealthGauge.value(), equalTo(status == UP ? 1.0d : 0.0d));
        });
    }
}
