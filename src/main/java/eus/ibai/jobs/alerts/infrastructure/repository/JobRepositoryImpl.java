package eus.ibai.jobs.alerts.infrastructure.repository;

import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.repository.JobRepository;
import eus.ibai.jobs.alerts.infrastructure.model.JobEntity;
import eus.ibai.jobs.alerts.infrastructure.model.JobSiteEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricUtils.recordActiveJobs;

@Slf4j
@Component
@AllArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final JobSiteEntityRepository jobSiteEntityRepository;

    private final JobEntityRepository jobEntityRepository;

    private final NewRelicRegistry meterRegistry;

    @Override
    public Mono<Void> sync(JobSiteSummary siteSummary) {
        String siteName = siteSummary.siteName();
        log.debug("Synchronizing Jobs from site {}. Total jobs: {}", siteName, siteSummary.jobs().size());
        Mono<JobSiteEntity> jobSiteEntitySource = jobSiteEntityRepository.findByName(siteName).cache();
        return disableOutdatedJobs(siteSummary)
                .thenMany(Flux.fromIterable(siteSummary.jobs()))
                .flatMap(job -> syncJob(job, jobSiteEntitySource))
                .then()
                .doOnSuccess(result -> {
                    log.info("Synchronized jobs from site {}", siteName);
                    recordActiveJobs(meterRegistry, siteName, siteSummary.jobs().size());
                });
    }

    @Override
    public Flux<Job> getEnabledJobsBySiteName(String siteName) {
        return jobEntityRepository.findByJobSiteNameAndEnabledOrderById(siteName)
                .map(jobEntity -> new Job(jobEntity.getTitle(), jobEntity.getUrl()));
    }

    private Mono<Void> disableOutdatedJobs(JobSiteSummary siteSummary) {
        return jobEntityRepository.findByJobSiteNameAndEnabledOrderById(siteSummary.siteName())
                .filter(jobEntity -> siteSummary.jobs().stream()
                        .noneMatch(job -> job.getTitle().equals(jobEntity.getTitle())))
                .flatMap(jobEntity -> jobEntityRepository.disableByTitle(jobEntity.getTitle())
                        .doOnSuccess(result -> log.debug("Disabled Job {} from site {}", jobEntity.getTitle(), siteSummary.siteName())))
                .then();
    }

    private Mono<JobEntity> syncJob(Job job, Mono<JobSiteEntity> jobSiteEntitySource) {
        return jobEntityRepository.findByTitle(job.getTitle())
                .flatMap(jobEntity -> {
                    log.trace("Job found in database: {}", jobEntity.getTitle());
                    if (!isSameJob(job, jobEntity)) {
                        jobEntity.setTitle(job.getTitle());
                        jobEntity.setUrl(job.getUrl());
                        jobEntity.setEnabled(true);
                        log.debug("Updating existing Job: {}", jobEntity.getTitle());
                        return jobEntityRepository.save(jobEntity);
                    }
                    return Mono.just(jobEntity);
                })
                .switchIfEmpty(Mono.defer(() -> jobSiteEntitySource.flatMap(jobSiteEntity ->
                        jobEntityRepository.save(new JobEntity(job.getTitle(), job.getUrl(), true, jobSiteEntity.getId()))
                                .doOnSuccess(jobEntity -> log.info("Persisted new job {} on site {}", jobEntity.getTitle(), jobSiteEntity.getName())))));
    }

    private boolean isSameJob(Job job, JobEntity jobEntity) {
        return job.getTitle().equals(jobEntity.getTitle()) && Objects.equals(job.getUrl(), jobEntity.getUrl()) && jobEntity.isEnabled();
    }
}
