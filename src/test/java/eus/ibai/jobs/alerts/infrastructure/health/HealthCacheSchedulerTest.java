package eus.ibai.jobs.alerts.infrastructure.health;

import eus.ibai.jobs.alerts.infrastructure.database.DatabaseHealthContributor;
import eus.ibai.jobs.alerts.infrastructure.email.EmailClient;
import eus.ibai.jobs.alerts.infrastructure.email.MailHealthContributor;
import eus.ibai.jobs.alerts.infrastructure.telegram.TelegramClient;
import eus.ibai.jobs.alerts.infrastructure.telegram.TelegramHealthContributor;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthCacheSchedulerTest {

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private EmailClient emailClient;

    @Mock
    private ConnectionFactory connectionFactory;

    private HealthCache healthCache;

    private HealthCacheScheduler healthCacheScheduler;

    @BeforeEach
    void beforeEach() {
        healthCache = new HealthCache();
        TelegramHealthContributor telegramHealthContributor = new TelegramHealthContributor(telegramClient);
        MailHealthContributor mailHealthContributor = new MailHealthContributor(emailClient);
        DatabaseHealthContributor databaseHealthContributor = new DatabaseHealthContributor(connectionFactory);
        healthCacheScheduler = new HealthCacheScheduler(healthCache, List.of(databaseHealthContributor, telegramHealthContributor, mailHealthContributor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"database", "telegram", "mail"})
    void should_return_all_components_healthy_when_application_starts(String componentName) {
        Health expectedHealth = Health.up().build();

        healthCacheScheduler.initCacheOptimistically();

        Health componentHealth = healthCache.getHealth(componentName);
        assertThat(componentHealth, equalTo(expectedHealth));
    }

    @Test
    void should_update_health_cache_when_checking_database_health_periodically() {
        healthCacheScheduler.initCacheOptimistically();
        Health expectedHealth = Health.down().build();

        healthCacheScheduler.checkDatabaseHealth();

        Health componentHealth = healthCache.getHealth("database");
        assertThat(componentHealth, equalTo(expectedHealth));
    }

    @Test
    void should_update_health_cache_when_checking_telegram_health_periodically() {
        healthCacheScheduler.initCacheOptimistically();
        when(telegramClient.checkHealth()).thenReturn(Mono.error(new Throwable()));

        healthCacheScheduler.checkTelegramHealth();

        Health componentHealth = healthCache.getHealth("telegram");
        assertThat(componentHealth, equalTo(Health.down().build()));
    }

    @Test
    void should_update_health_cache_when_checking_mail_health_periodically() {
        healthCacheScheduler.initCacheOptimistically();
        when(emailClient.checkHealth()).thenReturn(Mono.error(new Throwable()));

        healthCacheScheduler.checkMailHealth();

        Health componentHealth = healthCache.getHealth("mail");
        assertThat(componentHealth, equalTo(Health.down().build()));
    }
}