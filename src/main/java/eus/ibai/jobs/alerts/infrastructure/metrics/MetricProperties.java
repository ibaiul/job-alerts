package eus.ibai.jobs.alerts.infrastructure.metrics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "management")
public class MetricProperties {

    Metrics metrics;

    Observations observations;

    record Metrics(List<String> exclude) {}

    record Observations(List<String> exclude) {}
}