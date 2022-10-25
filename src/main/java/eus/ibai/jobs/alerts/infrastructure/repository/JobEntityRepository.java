package eus.ibai.jobs.alerts.infrastructure.repository;

import eus.ibai.jobs.alerts.infrastructure.model.JobEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JobEntityRepository extends ReactiveCrudRepository<JobEntity, Long> {

    Mono<JobEntity> findByTitle(String title);

    @Query("SELECT * FROM job j LEFT JOIN job_site js ON j.site_id = js.id WHERE js.name = :siteName AND j.enabled = true ORDER BY j.id ASC")
    Flux<JobEntity> findByJobSiteNameAndEnabledOrderById(@Param("siteName") String siteName);

    @Modifying
    @Query("UPDATE job SET enabled = false WHERE title = :title")
    Mono<Void> disableByTitle(@Param("title") String title);
}
