package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.infrastructure.health.HealthCache;
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

public class TelegramHealthIndicator extends AbstractReactiveHealthIndicator {

    private final HealthCache healthCache;

    public TelegramHealthIndicator(HealthCache healthCache) {
        super("Telegram is unhealthy.");
        this.healthCache = healthCache;
    }

    @Override
    protected Mono<Health> doHealthCheck(Health.Builder builder) {
        return Mono.just(healthCache.getHealth(TelegramHealthContributor.COMPONENT_NAME));
    }
}
