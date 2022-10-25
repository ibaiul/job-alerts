package eus.ibai.jobs.alerts.domain.repository;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JobRepository {

    Mono<Void> sync(JobSiteSummary siteSummary);

    Flux<Job> getEnabledJobsBySiteName(String siteName);
}
