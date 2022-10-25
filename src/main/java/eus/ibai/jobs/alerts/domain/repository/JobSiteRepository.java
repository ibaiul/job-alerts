package eus.ibai.jobs.alerts.domain.repository;

import eus.ibai.jobs.alerts.domain.JobSite;
import reactor.core.publisher.Mono;

public interface JobSiteRepository {

    Mono<JobSite> sync(JobSite jobSite);
}
