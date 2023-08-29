package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.Status;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.actuate.health.Status.UP;

public class MetricTestUtils {

    public static void verifyActiveJobsRecorded(MeterRegistry meterRegistry, String siteName, int activeJobs) {
        verifyActiveJobsRecorded(meterRegistry, siteName, activeJobs, 7);
    }

    public static void verifyActiveJobsRecorded(MeterRegistry meterRegistry, String siteName, int activeJobs, int waitSeconds) {
        await().atMost(waitSeconds, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            Gauge activeJobsGauge = meterRegistry.find("jobs.active")
                    .tag("site_name", siteName)
                    .gauge();
            assertThat(activeJobsGauge, notNullValue());
            assertThat(activeJobsGauge.value(), equalTo((double) activeJobs));
        });
    }

    public static void verifyComponentHealthRecorded(MeterRegistry meterRegistry, String componentName, Status status) {
        await().atMost(7, SECONDS).ignoreExceptions().untilAsserted(() -> {
            Gauge componentHealthGauge = meterRegistry.find("health")
                    .tag("component", componentName)
                    .gauge();
            assertThat(componentHealthGauge, notNullValue());
            assertThat(componentHealthGauge.value(), equalTo(status == UP ? 1.0d : 0.0d));
        });
    }
}
