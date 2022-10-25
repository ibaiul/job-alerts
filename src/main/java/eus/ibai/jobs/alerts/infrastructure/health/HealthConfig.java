package eus.ibai.jobs.alerts.infrastructure.health;

import eus.ibai.jobs.alerts.infrastructure.email.MailHealthIndicator;
import eus.ibai.jobs.alerts.infrastructure.telegram.TelegramHealthIndicator;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class HealthConfig {

    private final HealthCache healthCache;

    @Bean
    @ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
    public MailHealthIndicator mailHealthIndicator() {
        return new MailHealthIndicator(healthCache);
    }

    @Bean
    @ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
    public TelegramHealthIndicator telegramHealthIndicator() {
        return new TelegramHealthIndicator(healthCache);
    }
}
