package eus.ibai.jobs.alerts.infrastructure.health;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@EnableAsync
@AllArgsConstructor
public class HealthCacheScheduler {

    private final HealthCache healthCache;

    private final List<ComponentHealthContributor> componentHealthContributors;

    @PostConstruct
    public void initCacheOptimistically() {
        componentHealthContributors.forEach(componentHealthContributor -> healthCache.setHealth(componentHealthContributor.getComponentName(), Health.up().build()));
    }

    @Async
    @Scheduled(fixedDelayString = "${application.scheduler.health.delay}")
    public void checkHealth() {
        Flux.fromIterable(componentHealthContributors)
                .doOnNext(healthContributor -> log.trace("Checking health of {} component.", healthContributor.getComponentName()))
                .flatMap(componentHealthContributor -> componentHealthContributor.doHealthCheck()
                        .map(health -> Tuples.of(componentHealthContributor.getComponentName(), health)))
                .doOnNext(componentTuple -> log.trace("Updating health cache entry: {} -> {}", componentTuple.getT1(), componentTuple.getT2().getStatus()))
                .subscribe(componentTuple -> healthCache.setHealth(componentTuple.getT1(), componentTuple.getT2()));
    }
}
