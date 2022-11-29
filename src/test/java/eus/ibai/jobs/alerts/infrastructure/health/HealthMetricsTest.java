package eus.ibai.jobs.alerts.infrastructure.health;

import eus.ibai.jobs.alerts.AcceptanceTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.annotation.DirtiesContext;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricTestUtils.verifyComponentHealthRecorded;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HealthMetricsTest extends AcceptanceTest {

    @ParameterizedTest
    @ValueSource(strings = {"database", "telegram", "mail", "liveness", "readiness"})
    void should_record_component_health_metrics_when_application_is_running(String componentName) {
        verifyComponentHealthRecorded(meterRegistry, componentName, Status.UP);
    }
}
