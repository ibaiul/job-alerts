package eus.ibai.jobs.alerts.infrastructure.metrics;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
@AutoConfigureBefore({
        CompositeMeterRegistryAutoConfiguration.class,
        SimpleMetricsExportAutoConfiguration.class
})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass(NewRelicRegistry.class)
public class NewRelicMetricsExportAutoConfiguration {

    private final String appName;

    private final String licenseKey;

    private final String metricIngestUri;

    private final int metricStepInSeconds;

    public NewRelicMetricsExportAutoConfiguration(@Value("${newrelic.app.name}") String appName,
                                                  @Value("${newrelic.key.license}") String licenseKey,
                                                  @Value("${newrelic.metric.ingest.uri}") String metricIngestUri,
                                                  @Value("${newrelic.metric.step}") int metricStepInSeconds) {
        this.appName = appName;
        this.licenseKey = licenseKey;
        this.metricIngestUri = metricIngestUri;
        this.metricStepInSeconds = metricStepInSeconds;
    }

    @Bean
    public NewRelicRegistryConfig newRelicConfig() {
        return new NewRelicRegistryConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String uri() {
                return metricIngestUri;
            }

            @Override
            public String apiKey() {
                return licenseKey;
            }

            @Override
            public boolean useLicenseKey() {
                return true;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(metricStepInSeconds);
            }

            @Override
            public String serviceName() {
                return appName;
            }
        };
    }

    @Bean
    public NewRelicRegistry newRelicMeterRegistry(NewRelicRegistryConfig config) throws UnknownHostException {
        NewRelicRegistry newRelicRegistry = NewRelicRegistry.builder(config)
                        .commonAttributes(new Attributes().put("host", InetAddress.getLocalHost().getHostName()))
                        .build();
        newRelicRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm.threads"));
        newRelicRegistry.start(new NamedThreadFactory("newrelic.micrometer.registry"));
        return newRelicRegistry;
    }
}
