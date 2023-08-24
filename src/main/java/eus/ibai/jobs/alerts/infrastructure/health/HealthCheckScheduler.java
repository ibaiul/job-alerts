package eus.ibai.jobs.alerts.infrastructure.health;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Component
@EnableAsync
@AllArgsConstructor
public class HealthCheckScheduler {

    private final HealthCache healthCache;

    private final List<ComponentHealthContributor> componentHealthContributors;

    @PostConstruct
    public void initCacheOptimistically() {
        componentHealthContributors.forEach(componentHealthContributor -> healthCache.setHealth(componentHealthContributor.getComponentName(), Health.up().build()));
    }

    @Async
    @Scheduled(fixedDelayString = "${management.health.r2dbc.interval}", timeUnit = SECONDS)
    public void checkDatabaseHealth() {
        checkComponentHealth("database");
    }

    @Async
    @Scheduled(fixedDelayString = "${management.health.telegram.interval}", timeUnit = SECONDS)
    @ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
    public void checkTelegramHealth() {
        checkComponentHealth("telegram");
    }

    @Async
    @Scheduled(fixedDelayString = "${management.health.mail.interval}", timeUnit = SECONDS)
    @ConditionalOnProperty(prefix = "mail", name = "enabled", havingValue = "true")
    public void checkMailHealth() {
        checkComponentHealth("mail");
    }

    private void checkComponentHealth(String componentName) {
        Flux.fromIterable(componentHealthContributors)
                .filter(componentHealthContributor -> componentName.equals(componentHealthContributor.getComponentName()))
                .doOnNext(healthContributor -> log.trace("Checking health of {} component.", healthContributor.getComponentName()))
                .flatMap(componentHealthContributor -> componentHealthContributor.doHealthCheck()
                        .map(health -> Tuples.of(componentHealthContributor.getComponentName(), health)))
                .doOnNext(componentTuple -> log.trace("Updating health cache entry: {} -> {}", componentTuple.getT1(), componentTuple.getT2().getStatus()))
                .subscribe(componentTuple -> healthCache.setHealth(componentTuple.getT1(), componentTuple.getT2()));
    }
}
