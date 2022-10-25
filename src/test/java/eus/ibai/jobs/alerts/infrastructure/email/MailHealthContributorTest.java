package eus.ibai.jobs.alerts.infrastructure.email;

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
class MailHealthContributorTest {

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private MailHealthContributor healthContributor;

    @Test
    void should_return_healthy_when_mail_server_available() {
        when(emailClient.checkHealth()).thenReturn(Mono.empty());

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(Health.up().build())
                .verifyComplete();
    }

    @Test
    void should_return_unhealthy_when_mail_server_unavailable() {
        when(emailClient.checkHealth()).thenReturn(Mono.error(new NotificationException("")));

        StepVerifier.create(healthContributor.doHealthCheck())
                .expectNext(Health.down().build())
                .verifyComplete();
    }
}