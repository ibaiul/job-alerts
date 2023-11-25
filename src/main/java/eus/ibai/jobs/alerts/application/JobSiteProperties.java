package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.domain.notification.NotificationTargets;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Value
@Component
@ConfigurationProperties
public class JobSiteProperties {

    List<JobSiteDefinition> sites;

    public record JobSiteDefinition(String name, String url, Map<String, Object> strategy, List<NotificationTargets> notifications) {

        public String name() {
            return name.replace("_", " ");
        }
    }
}
