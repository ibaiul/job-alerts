package eus.ibai.jobs.alerts.domain;

import eus.ibai.jobs.alerts.domain.parse.JobParsingStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class JobSite {

    private final String name;

    private final String url;

    private final JobParsingStrategy parsingStrategy;

    private final String steps;

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

    public String getParsingStrategyType() {
        return parsingStrategy.getType();
    }

    public String getParsingStrategySteps() {
        return steps;
    }

    private Flux<Job> getJobs() {
        return parsingStrategy.parseJobs(url, steps);
    }
}
