package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.health.Status;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.boot.actuate.health.Status.UP;

public final class MetricUtils {

    private static final Map<String, AtomicInteger> activeJobsMap = new ConcurrentHashMap<>();

    private MetricUtils() {
    }

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
