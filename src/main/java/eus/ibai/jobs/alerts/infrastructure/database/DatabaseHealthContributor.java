package eus.ibai.jobs.alerts.infrastructure.database;

import eus.ibai.jobs.alerts.infrastructure.health.ComponentHealthContributor;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.r2dbc.ConnectionFactoryHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DatabaseHealthContributor implements ComponentHealthContributor {

    public static final String COMPONENT_NAME = "database";

    private final ConnectionFactoryHealthIndicator decoratedIndicator;

    public DatabaseHealthContributor(ConnectionFactory connectionFactory) {
        this.decoratedIndicator = new ConnectionFactoryHealthIndicator(connectionFactory);
    }

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public Mono<Health> doHealthCheck() {
        return decoratedIndicator.health()
                .map(health -> Health.status(health.getStatus()).build())
                .doOnNext(health -> log.debug("Received database health response: {}", health.getStatus()));
    }
}
