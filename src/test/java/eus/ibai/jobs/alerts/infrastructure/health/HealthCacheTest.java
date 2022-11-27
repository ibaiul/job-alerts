package eus.ibai.jobs.alerts.infrastructure.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import static eus.ibai.jobs.alerts.TestData.COMPONENT_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class HealthCacheTest {

    private HealthCache healthCache;

    @BeforeEach
    void beforeEach() {
        healthCache = new HealthCache();
    }

    @Test
    void should_return_health_of_component() {
        Health expectedHealth = Health.up().build();

        healthCache.setHealth(COMPONENT_NAME, expectedHealth);

        Health actualHealth = healthCache.getHealth(COMPONENT_NAME);
        assertThat(actualHealth, equalTo(expectedHealth));
    }

    @Test
    void should_return_unknown_when_getting_health_of_unregistered_component() {
        Health health = healthCache.getHealth("notRegisteredComponent");

        assertThat(health, equalTo(Health.unknown().build()));
    }
}