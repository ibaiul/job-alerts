package eus.ibai.jobs.alerts.infrastructure.telegram;

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

class TelegramHealthContributorTest {

    private TelegramClient telegramClient;

    private MeterRegistry meterRegistry;

    private TelegramHealthContributor healthContributor;

    @BeforeEach
    void beforeEach() {
        telegramClient = mock(TelegramClient.class);
        meterRegistry = new SimpleMeterRegistry();
        healthContributor = new TelegramHealthContributor(telegramClient, meterRegistry);
    }

    @Test
    void should_return_healthy_when_telegram_available() {
        when(telegramClient.checkHealth()).thenReturn(Mono.just("{}"));
        Health expectedHealth = Health.up().build();

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(expectedHealth)
                .verifyComplete();
        verifyComponentHealthMetricRecorded(meterRegistry, healthContributor.getComponentName(), expectedHealth.getStatus());
    }

    @Test
    void should_return_unhealthy_when_telegram_unavailable() {
        when(telegramClient.checkHealth()).thenReturn(Mono.error(new NotificationException("")));
        Health expectedHealth = Health.down().build();

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(expectedHealth)
                .verifyComplete();
        verifyComponentHealthMetricRecorded(meterRegistry, healthContributor.getComponentName(), expectedHealth.getStatus());
    }
}