package eus.ibai.jobs.alerts.infrastructure.email;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "email")
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
public class EmailProperties {

    private String from;
}
