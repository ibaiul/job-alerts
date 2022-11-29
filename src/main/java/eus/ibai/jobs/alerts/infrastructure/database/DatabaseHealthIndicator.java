package eus.ibai.jobs.alerts.infrastructure.database;

import eus.ibai.jobs.alerts.infrastructure.health.HealthCache;
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DatabaseHealthIndicator extends AbstractReactiveHealthIndicator {

    private final HealthCache healthCache;

    public DatabaseHealthIndicator(HealthCache healthCache) {
        super("Database is unhealthy.");
        this.healthCache = healthCache;
    }

    @Override
    protected Mono<Health> doHealthCheck(Health.Builder builder) {
        return Mono.just(healthCache.getHealth(DatabaseHealthContributor.COMPONENT_NAME));
    }
}
