package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

import static org.springframework.boot.actuate.health.Status.UP;

public final class MetricUtils {

    private MetricUtils() {
    }

    public static void recordActiveJobs(MeterRegistry meterRegistry, String siteName, int activeJobCount) {
        meterRegistry.gauge("jobs.active", List.of(Tag.of("site_name", siteNameToTag(siteName))), activeJobCount);
    }

    public static void recordHealthcheck(MeterRegistry meterRegistry, String componentName, Status status) {
        meterRegistry.gauge("health.component", List.of(Tag.of("component", componentName), Tag.of("status", status.getCode())), mapHealthStatusToMetricValue(status));
    }

    private static String siteNameToTag(String siteName) {
        return siteName.replaceAll("\\s+", "_");
    }

    private static int mapHealthStatusToMetricValue(Status status) {
        return status == UP ? 1 : 0;
    }
}
