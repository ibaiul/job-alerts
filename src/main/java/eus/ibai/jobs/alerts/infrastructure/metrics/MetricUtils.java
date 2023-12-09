package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class MetricUtils {

    private static final Map<String, AtomicInteger> activeJobsMap = new ConcurrentHashMap<>();

    private MetricUtils() {}

    public static void recordActiveJobs(MeterRegistry meterRegistry, String siteName, int activeJobCount) {
        if (activeJobsMap.containsKey(siteName)) {
            activeJobsMap.get(siteName).set(activeJobCount);
        } else {
            AtomicInteger currentActiveJobs = new AtomicInteger(activeJobCount);
            activeJobsMap.put(siteName, currentActiveJobs);
            meterRegistry.gauge("jobs.active", List.of(Tag.of("site_name", siteNameToTag(siteName))), currentActiveJobs, AtomicInteger::get);
        }
    }

    public static void clearGaugeReferences() {
        activeJobsMap.keySet().clear();
    }

    private static String siteNameToTag(String siteName) {
        return siteName.replaceAll("\\s+", "_");
    }
}
