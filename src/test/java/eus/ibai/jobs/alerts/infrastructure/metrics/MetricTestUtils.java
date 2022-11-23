package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.hamcrest.CoreMatchers;
import org.springframework.boot.actuate.health.Status;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.actuate.health.Status.UP;

public class MetricTestUtils {

    public static void verifyActiveJobsRecorded(MeterRegistry meterRegistry, String siteName, int activeJobs) {
        await().atMost(7, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            Gauge activeJobsGauge = meterRegistry.find("jobs.active")
                    .tag("site_name", siteName)
                    .gauge();
            assertThat(activeJobsGauge, notNullValue());
            assertThat(activeJobsGauge.value(), equalTo((double) activeJobs));
        });
    }

    public static void verifyComponentHealthMetricRecorded(MeterRegistry meterRegistry, String componentName, Status status) {
        Gauge componentHealthGauge = meterRegistry.find("health.component")
                .tag("component", componentName)
                .tag("status", status.getCode())
                .gauge();
        assertThat(componentHealthGauge, CoreMatchers.notNullValue());
        assertThat(componentHealthGauge.value(), equalTo(status == UP ? 1.0d : 0.0d));
    }
}
