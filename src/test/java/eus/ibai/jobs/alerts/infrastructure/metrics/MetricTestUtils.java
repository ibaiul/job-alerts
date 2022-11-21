package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.hamcrest.Matchers;
import org.springframework.boot.actuate.health.Status;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.actuate.health.Status.UP;

public class MetricTestUtils {

    public static void verifyComponentHealthMetricRecorded(MeterRegistry meterRegistry, String componentName, Status status) {
        Gauge componentHealthGauge = meterRegistry.find("health.component")
                .tag("component", componentName)
                .tag("status", status.getCode())
                .gauge();
        assertThat(componentHealthGauge, notNullValue());
        assertThat(componentHealthGauge.value(), equalTo(status == UP ? 1.0d : 0.0d));
    }

    public static void verifyActiveJobsRecorded(MeterRegistry meterRegistry, String siteName, int activeJobs) {
        Gauge activeJobsGauge = meterRegistry.find("jobs.active")
                .tag("site_name", siteName).gauge();
        assertThat(activeJobsGauge, Matchers.notNullValue());
        assertThat(activeJobsGauge.value(), Matchers.equalTo((double) activeJobs));
    }
}
