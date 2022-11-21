package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.domain.notification.NotificationException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricTestUtils.verifyComponentHealthMetricRecorded;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailHealthContributorTest {

    private EmailClient emailClient;

    private MeterRegistry meterRegistry;

    private MailHealthContributor healthContributor;

    @BeforeEach
    void beforeEach() {
        emailClient = mock(EmailClient.class);
        meterRegistry = new SimpleMeterRegistry();
        healthContributor = new MailHealthContributor(emailClient, meterRegistry);
    }

    @Test
    void should_return_healthy_when_mail_server_available() {
        when(emailClient.checkHealth()).thenReturn(Mono.empty());
        Health expectedHealth = Health.up().build();

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(expectedHealth)
                .verifyComplete();
        verifyComponentHealthMetricRecorded(meterRegistry, healthContributor.getComponentName(), expectedHealth.getStatus());
    }

    @Test
    void should_return_unhealthy_when_mail_server_unavailable() {
        when(emailClient.checkHealth()).thenReturn(Mono.error(new NotificationException("")));
        Health expectedHealth = Health.down().build();

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(expectedHealth)
                .verifyComplete();
        verifyComponentHealthMetricRecorded(meterRegistry, healthContributor.getComponentName(), expectedHealth.getStatus());
    }
}