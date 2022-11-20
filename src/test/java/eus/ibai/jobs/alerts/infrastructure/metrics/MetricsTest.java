package eus.ibai.jobs.alerts.infrastructure.metrics;

import eus.ibai.jobs.alerts.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MetricsTest extends AcceptanceTest {

    @Value("${newrelic.metric.step}")
    private int metricStepInSeconds;

    @Test
    void should_send_metrics_for_ingestion_at_intervals_when_application_is_running() {
        int expectedMinRequests = 2;
        int waitInSeconds = metricStepInSeconds * expectedMinRequests + 1;

        await().atMost(waitInSeconds, SECONDS).ignoreExceptions().untilAsserted(() -> verifyMetricsSentAtLeast(expectedMinRequests));
    }
}