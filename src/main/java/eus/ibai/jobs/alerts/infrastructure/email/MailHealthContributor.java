package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.infrastructure.health.ComponentHealthContributor;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricUtils.recordHealthcheck;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
public class MailHealthContributor implements ComponentHealthContributor {

    public static final String COMPONENT_NAME = "mail";

    private final EmailClient emailClient;

    private final MeterRegistry meterRegistry;

    @Override
    public Mono<Health> doHealthCheck() {
        return emailClient.checkHealth()
                .thenReturn(Health.up().build())
                .onErrorReturn(Health.down().build())
                .doOnNext(health -> recordHealthcheck(meterRegistry, COMPONENT_NAME, health.getStatus()));
    }

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }
}
