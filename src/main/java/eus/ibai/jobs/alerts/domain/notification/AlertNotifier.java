package eus.ibai.jobs.alerts.domain.notification;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AlertNotifier {

    Mono<Void> alertSiteUpdated(JobSiteSummary jobSiteSummary, List<Job> previousJobs);

    Mono<Void> alertWeeklySummary(JobSiteSummary jobSiteSummary);
}
