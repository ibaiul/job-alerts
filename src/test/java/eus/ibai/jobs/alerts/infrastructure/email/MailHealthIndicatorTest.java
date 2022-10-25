package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.infrastructure.health.HealthCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailHealthIndicatorTest {

    @Mock
    private HealthCache healthCache;

    @InjectMocks
    private MailHealthIndicator mailHealthIndicator;

    @Test
    void should_indicate_healthy_when_last_known_state_from_cache_is_healthy() {
        Health healthy = Health.up().build();
        when(healthCache.getHealth(MailHealthContributor.COMPONENT_NAME)).thenReturn(healthy);

        StepVerifier.create(mailHealthIndicator.health())
                .expectNext(healthy)
                .verifyComplete();
    }

    @Test
    void should_indicate_unhealthy_when_last_known_state_from_cache_is_unhealthy() {
        Health unhealthy = Health.down().build();
        when(healthCache.getHealth(MailHealthContributor.COMPONENT_NAME)).thenReturn(unhealthy);

        StepVerifier.create(mailHealthIndicator.health())
                .expectNext(unhealthy)
                .verifyComplete();
    }

    @Test
    void should_indicate_unhealthy_when_cannot_retrieve_last_known_state_from_cache() {
        when(healthCache.getHealth(MailHealthContributor.COMPONENT_NAME)).thenReturn(null);

        StepVerifier.create(mailHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus() == Status.DOWN)
                .verifyComplete();
    }
}