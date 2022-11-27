package eus.ibai.jobs.alerts.infrastructure.telegram;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.metrics.AutoTimer;
import org.springframework.boot.actuate.metrics.web.reactive.client.MetricsWebClientFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Bean
    public MetricsWebClientFilterFunction telegramMetricWebClientFilter(MeterRegistry meterRegistry) {
        return new MetricsWebClientFilterFunction(meterRegistry, new TelegramWebClientExchangeTagsProvider(), "http.out.telegram", AutoTimer.ENABLED);
    }
}
