package eus.ibai.jobs.alerts.domain.alert;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JobSiteAlerterRegistry {

    private final Map<String, JobSiteAlerter> alerters = new ConcurrentHashMap<>();

    private JobSiteAlerterRegistry() {
    }

    public void registerJobSiteAlerter(JobSiteAlerter jobSiteAlerter) {
        alerters.put(jobSiteAlerter.getSiteName(), jobSiteAlerter);
    }

    public List<JobSiteAlerter> getAll() {
        return new ArrayList<>(alerters.values());
    }
}
