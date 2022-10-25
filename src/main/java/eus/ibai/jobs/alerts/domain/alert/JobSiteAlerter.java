package eus.ibai.jobs.alerts.domain.alert;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSite;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifier;
import eus.ibai.jobs.alerts.domain.repository.JobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class JobSiteAlerter {

    private final JobSite jobSite;

    private final List<AlertNotifier> alertNotifiers;

    private final JobRepository jobRepository;

    public Mono<JobSiteSummary> checkSite() {
        log.debug("Checking for updates on site {}", jobSite.getName());
        return jobSite.getSummary()
                .flatMap(summary -> checkIfJobSiteUpdated(summary).thenReturn(summary));
    }

    public Mono<JobSiteSummary> weeklySummary() {
        log.debug("Running weekly summary for site {}", jobSite.getName());
        return jobSite.getSummary()
                .flatMap(summary -> triggerWeeklySummaryAlert(summary).thenReturn(summary));
    }

    public String getSiteName() {
        return jobSite.getName();
    }

    private Mono<Void> checkIfJobSiteUpdated(JobSiteSummary summary) {
        return jobRepository.getEnabledJobsBySiteName(jobSite.getName())
                .collectList()
                .filter(previousJobs -> isJobSiteUpdated(previousJobs, new ArrayList<>(summary.jobs())))
                .flatMap(previousJobs -> triggerJobSiteUpdatedAlert(summary, previousJobs))
                .then();
    }

    private boolean isJobSiteUpdated(List<Job> previousJobs, List<Job> currentJobs) {
        log.debug("Previous jobs {}, current jobs {}", previousJobs.size(), currentJobs.size());
        previousJobs.sort(Comparator.comparing(Job::getTitle));
        currentJobs.sort(Comparator.comparing(Job::getTitle));
        return !previousJobs.equals(currentJobs);
    }

    private Mono<Void> triggerJobSiteUpdatedAlert(JobSiteSummary summary, List<Job> previousJobs) {
        return Flux.fromIterable(alertNotifiers)
                .flatMap(alertNotifier -> alertNotifier.alertSiteUpdated(summary, previousJobs))
                .then();
    }

    private Mono<Void> triggerWeeklySummaryAlert(JobSiteSummary summary) {
        return Flux.fromIterable(alertNotifiers)
                .flatMap(alertNotifier -> alertNotifier.alertWeeklySummary(summary))
                .then();
    }
}
