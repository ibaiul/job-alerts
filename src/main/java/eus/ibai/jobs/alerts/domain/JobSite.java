package eus.ibai.jobs.alerts.domain;

import eus.ibai.jobs.alerts.domain.parse.JobParsingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class JobSite {

    private final String name;

    private final String url;

    private final JobParsingStrategy parsingStrategy;

    public Mono<JobSiteSummary> getSummary() {
        return getJobs()
                .distinct(Job::getTitle)
                .doOnDiscard(Job.class, job -> log.warn("Duplicate job title found in summary. Discarding element: {} from {}", job, name))
                .collectList()
                .map(jobs -> new JobSiteSummary(getName(), getUrl(), jobs));
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    private Flux<Job> getJobs() {
        return parsingStrategy.parseJobs(url);
    }
}
