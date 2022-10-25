package eus.ibai.jobs.alerts.infrastructure.telegram;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram")
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramProperties {

    private String baseUrl;
    private String botKey;
}
