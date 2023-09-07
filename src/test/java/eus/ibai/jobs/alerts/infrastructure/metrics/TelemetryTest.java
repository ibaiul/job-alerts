package eus.ibai.jobs.alerts.infrastructure.metrics;

import eus.ibai.jobs.alerts.AcceptanceTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricTestUtils.verifyMetricNamePrefixNotRecorded;

@ActiveProfiles("ot")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TelemetryTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @ParameterizedTest
    @ValueSource(strings = {"jvm", "spring.data"})
    void should_not_record_excluded_metrics(String metricNamePrefix) {
        verifyMetricNamePrefixNotRecorded(meterRegistry, metricNamePrefix);
    }

    @ParameterizedTest
    @ValueSource(strings = {"http.server.requests", "http.out.telegram"})
    void should_not_record_excluded_observations(String metricNamePrefix) {
        webTestClient.get()
                .uri("/foo/bar")
                .exchange();
        verifyMetricNamePrefixNotRecorded(meterRegistry, metricNamePrefix);
    }
}
