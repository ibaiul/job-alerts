package eus.ibai.jobs.alerts.infrastructure.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthCacheTest {

    private static final String COMPONENT_NAME = "componentName";

    @Mock
    private ComponentHealthContributor healthContributor;

    private HealthCache healthCache;

    @BeforeEach
    void beforeEach() {
        when(healthContributor.getComponentName()).thenReturn(COMPONENT_NAME);
        healthCache = new HealthCache(List.of(healthContributor));
    }

    @Test
    void should_return_all_components_healthy_when_starting() {
        Health expectedHealth = Health.up().build();
        Health componentHealth = healthCache.getHealth(COMPONENT_NAME);

        assertThat(componentHealth, equalTo(expectedHealth));
    }

    @Test
    void should_update_internal_cache_when_checking_health_periodically() {
        Health expectedHealth = Health.down().build();
        when(healthContributor.doHealthCheck()).thenReturn(Mono.just(expectedHealth));

        healthCache.checkHealth();

        Health componentHealth = healthCache.getHealth(COMPONENT_NAME);
        assertThat(componentHealth, equalTo(expectedHealth));
    }
}