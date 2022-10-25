package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.infrastructure.health.HealthCache;
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

public class MailHealthIndicator extends AbstractReactiveHealthIndicator {

    private final HealthCache healthCache;

    public MailHealthIndicator(HealthCache healthCache) {
        super("Mail is unhealthy.");
        this.healthCache = healthCache;
    }

    @Override
    protected Mono<Health> doHealthCheck(Health.Builder builder) {
        return Mono.just(healthCache.getHealth(MailHealthContributor.COMPONENT_NAME));
    }
}
