package eus.ibai.jobs.alerts.infrastructure.repository;

import eus.ibai.jobs.alerts.domain.JobSite;
import eus.ibai.jobs.alerts.domain.repository.JobSiteRepository;
import eus.ibai.jobs.alerts.infrastructure.model.JobSiteEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class JobSiteRepositoryImpl implements JobSiteRepository {

    private final JobSiteEntityRepository jobSiteEntityRepository;

    @Override
    public Mono<JobSite> sync(JobSite jobSite) {
        String siteName = jobSite.getName();
        return jobSiteEntityRepository.findByName(siteName)
                .flatMap(siteEntity -> Mono.just(jobSite)
                        .filter(js -> !isSameSite(jobSite, siteEntity))
                        .flatMap(js -> {
                            siteEntity.setName(siteName);
                            siteEntity.setUrl(jobSite.getUrl());
                            siteEntity.setParsingStrategyType(jobSite.getParsingStrategyType());
                            siteEntity.setParsingStrategySteps(jobSite.getParsingStrategySteps());
                            log.debug("Updating existing site {}", siteName);
                            return jobSiteEntityRepository.save(siteEntity);
                        })
                        .defaultIfEmpty(siteEntity)
                )
                .switchIfEmpty(Mono.defer(() -> jobSiteEntityRepository.save(new JobSiteEntity(null, siteName, jobSite.getUrl(), jobSite.getParsingStrategyType(), jobSite.getParsingStrategySteps()))))
                .thenReturn(jobSite);
    }

    private boolean isSameSite(JobSite jobSite, JobSiteEntity siteEntity) {
        return jobSite.getName().equals(siteEntity.getName()) && jobSite.getUrl().equals(siteEntity.getUrl())
                && jobSite.getParsingStrategyType().equals(siteEntity.getParsingStrategyType())
                && jobSite.getParsingStrategySteps().equals(siteEntity.getParsingStrategySteps());
    }
}
