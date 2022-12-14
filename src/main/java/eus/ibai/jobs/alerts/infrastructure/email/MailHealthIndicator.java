package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.infrastructure.health.HealthCache;
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
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
