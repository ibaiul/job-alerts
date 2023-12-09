package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.application.JobSiteProperties.JobSiteDefinition;
import eus.ibai.jobs.alerts.domain.JobSite;
import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerter;
import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerterRegistry;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifierProvider;
import eus.ibai.jobs.alerts.domain.parse.JobParsingStrategy;
import eus.ibai.jobs.alerts.domain.parse.JobParsingStrategyFactory;
import eus.ibai.jobs.alerts.domain.repository.JobRepository;
import eus.ibai.jobs.alerts.domain.repository.JobSiteRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@DependsOn({"flyway"})
@AllArgsConstructor
public class JobSiteRegistration {

    private final JobSiteAlerterRegistry jobSiteAlerterRegistry;

    private final AlertNotifierProvider alertNotifierProvider;

    private final JobSiteProperties jobSiteProperties;

    private final JobSiteRepository jobSiteRepository;

    private final JobRepository jobRepository;

    private final JobParsingStrategyFactory jobParsingStrategyFactory;

    @PostConstruct
    public void registerJobSites() {
        Flux.fromIterable(jobSiteProperties.getSites())
                .doOnNext(siteDefinition -> log.debug("Registering JobSiteDefinition for {}", siteDefinition.name()))
                .flatMap(siteDefinition -> syncSite(siteDefinition)
                        .then(Mono.defer(() -> registerJobSiteAlerter(siteDefinition))))
                .onErrorContinue((error, o) -> log.error("Failed to register site.", error))
                .subscribe();
    }

    private Mono<Void> syncSite(JobSiteDefinition jobSiteDefinition) {
        JobSite jobSite = toJobSite(jobSiteDefinition);
        return jobSiteRepository.sync(jobSite)
                .doOnSuccess(persistedJobSite -> log.info("Synchronized JobSiteDefinition for {}", persistedJobSite.getName()))
                .then();
    }

    private Mono<Void> registerJobSiteAlerter(JobSiteDefinition jobSiteDefinition) {
        return Mono.just(jobSiteDefinition)
                .map(siteDefinition -> alertNotifierProvider.createNotifiers(jobSiteDefinition.notifications()))
                .map(alertNotifiers -> {
                    JobSite jobSite = toJobSite(jobSiteDefinition);
                    return new JobSiteAlerter(jobSite, alertNotifiers, jobRepository);
                })
                .doOnSuccess(jobSiteAlerter -> {
                    jobSiteAlerterRegistry.registerJobSiteAlerter(jobSiteAlerter);
                    log.info("Registered job site alerter for {}", jobSiteAlerter.getSiteName());
                })
                .then();
    }

    private JobSite toJobSite(JobSiteDefinition jobSiteDefinition) {
        Map<String, Object> parsingStrategySettings = jobSiteDefinition.strategy();
        JobParsingStrategy parsingStrategy = jobParsingStrategyFactory.getStrategy(parsingStrategySettings);
        return new JobSite(jobSiteDefinition.name(), jobSiteDefinition.url(), parsingStrategy);
    }
}
