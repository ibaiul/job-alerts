package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.infrastructure.health.ComponentHealthContributor;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramHealthContributor implements ComponentHealthContributor {

    public static final String COMPONENT_NAME = "telegram";

    private final TelegramClient telegramClient;

    @Override
    public Mono<Health> doHealthCheck() {
        return telegramClient.checkHealth()
                .thenReturn(Health.up().build())
                .onErrorReturn(Health.down().build());
    }

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }
}
