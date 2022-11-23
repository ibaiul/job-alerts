package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.infrastructure.health.ComponentHealthContributor;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricUtils.recordComponentHealth;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramHealthContributor implements ComponentHealthContributor {

    public static final String COMPONENT_NAME = "telegram";

    private final TelegramClient telegramClient;

    private final MeterRegistry meterRegistry;

    @Override
    public Mono<Health> doHealthCheck() {
        return telegramClient.checkHealth()
                .thenReturn(Health.up().build())
                .onErrorReturn(Health.down().build())
                .doOnNext(health -> recordComponentHealth(meterRegistry, COMPONENT_NAME, health.getStatus()));
    }

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }
}
