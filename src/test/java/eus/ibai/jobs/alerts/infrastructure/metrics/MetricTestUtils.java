package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class MetricTestUtils {

    public static void verifyActiveJobsRecorded(MeterRegistry meterRegistry, String siteName, int activeJobs) {
        await().atMost(5, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            Gauge activeJobsGauge = meterRegistry.find("jobs.active")
                    .tag("site_name", siteName)
                    .gauge();
            assertThat(activeJobsGauge, notNullValue());
            assertThat(activeJobsGauge.value(), is((double) activeJobs));
        });
    }
}
