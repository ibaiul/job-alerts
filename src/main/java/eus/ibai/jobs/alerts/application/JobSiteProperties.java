package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.domain.notification.NotificationTargets;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Value
@Component
@ConfigurationProperties
public class JobSiteProperties {

    List<JobSiteDefinition> sites;

    public record JobSiteDefinition(String name, String url, ParsingStrategy strategy, List<NotificationTargets> notifications) {

        public String name() {
            return name.replace("_", " ");
        }

        public record ParsingStrategy(String type, String steps) {}
    }
}
