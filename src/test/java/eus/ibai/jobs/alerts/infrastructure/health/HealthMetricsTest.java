package eus.ibai.jobs.alerts.infrastructure.health;

import eus.ibai.jobs.alerts.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.annotation.DirtiesContext;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricTestUtils.verifyComponentHealthRecorded;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HealthMetricsTest extends AcceptanceTest {

    @Test
    void should_record_database_health_metrics_when_application_is_running() {
        verifyComponentHealthRecorded(meterRegistry, "database", Status.UP);
    }

    @Test
    void should_record_telegram_health_metrics_when_application_is_running() {
        verifyComponentHealthRecorded(meterRegistry, "telegram", Status.UP);
    }

    @Test
    void should_record_mail_health_metrics_when_application_is_running() {
        verifyComponentHealthRecorded(meterRegistry, "mail", Status.UP);
    }
}
