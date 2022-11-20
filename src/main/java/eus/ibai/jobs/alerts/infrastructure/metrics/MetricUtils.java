package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.List;

public final class MetricUtils {

    private MetricUtils() {
    }

    public static void recordActiveJobs(MeterRegistry meterRegistry, String siteName, int activeJobCount) {
        meterRegistry.gauge("jobs.active", List.of(Tag.of("site_name", siteNameToTag(siteName))), activeJobCount);
    }

    private static String siteNameToTag(String siteName) {
        return siteName.replaceAll("\\s+", "_");
    }
}
