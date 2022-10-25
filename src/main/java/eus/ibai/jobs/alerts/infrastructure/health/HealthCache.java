package eus.ibai.jobs.alerts.infrastructure.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@EnableAsync
public class HealthCache {

    private final List<ComponentHealthContributor> componentHealthContributors;

    private final Map<String, Health> lastKnownHealths = new ConcurrentHashMap<>();

    public HealthCache(List<ComponentHealthContributor> componentHealthContributors) {
        this.componentHealthContributors = componentHealthContributors;
        componentHealthContributors.forEach(componentHealthContributor -> lastKnownHealths.put(componentHealthContributor.getComponentName(), Health.up().build()));
    }

    public Health getHealth(String componentName) {
        return lastKnownHealths.get(componentName);
    }

    @Async
    @Scheduled(fixedDelayString = "${application.scheduler.health.delay}")
    public void checkHealth() {
        Flux.fromIterable(componentHealthContributors)
                .doOnNext(healthContributor -> log.trace("Checking health of {} component.", healthContributor.getComponentName()))
                .flatMap(componentHealthContributor -> componentHealthContributor.doHealthCheck()
                                                        .map(health -> Tuples.of(componentHealthContributor.getComponentName(), health)))
                .doOnNext(componentTuple -> log.trace("Updating health cache entry: {} -> {}", componentTuple.getT1(), componentTuple.getT2().getStatus()))
                .subscribe(componentTuple -> lastKnownHealths.put(componentTuple.getT1(), componentTuple.getT2()));
    }
}
