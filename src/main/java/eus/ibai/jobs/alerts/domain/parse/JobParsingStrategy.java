package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import reactor.core.publisher.Flux;

public interface JobParsingStrategy {

    String getType();

    Flux<Job> parseJobs(String url, String steps);
}
