package eus.ibai.jobs.alerts.infrastructure.repository;

import eus.ibai.jobs.alerts.infrastructure.model.JobSiteEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface JobSiteEntityRepository extends ReactiveCrudRepository<JobSiteEntity, Long> {

    Mono<JobSiteEntity> findByName(String name);
}
