package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.domain.notification.NotificationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramHealthContributorTest {

    @Mock
    private TelegramClient telegramClient;

    @InjectMocks
    private TelegramHealthContributor healthContributor;

    @Test
    void should_return_healthy_when_telegram_available() {
        when(telegramClient.checkHealth()).thenReturn(Mono.just("{}"));

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(Health.up().build())
                .verifyComplete();
    }

    @Test
    void should_return_unhealthy_when_telegram_unavailable() {
        when(telegramClient.checkHealth()).thenReturn(Mono.error(new NotificationException("")));

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(Health.down().build())
                .verifyComplete();
    }
}