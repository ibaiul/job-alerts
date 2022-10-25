package eus.ibai.jobs.alerts.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

public interface ComponentHealthContributor {

    String getComponentName();

    Mono<Health> doHealthCheck();
}
