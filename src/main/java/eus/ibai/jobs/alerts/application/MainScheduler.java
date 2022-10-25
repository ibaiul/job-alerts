package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerter;
import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerterRegistry;
import eus.ibai.jobs.alerts.domain.repository.JobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@DependsOn("jobSiteRegistration")
@AllArgsConstructor
public class MainScheduler {

    private final JobSiteAlerterRegistry jobSiteAlerterRegistry;

    private final JobRepository jobRepository;

    @Scheduled(cron = "${application.scheduler.periodic.cron}", zone = "${application.scheduler.periodic.zone}")
    public void runPeriodicSchedule() {
        log.debug("Periodic schedule started.");
        Flux.fromIterable(jobSiteAlerterRegistry.getAll())
                .flatMap(JobSiteAlerter::checkSite)
                .flatMap(jobRepository::sync)
                .onErrorContinue((error, o) -> log.error("Failed to check site while running periodic schedule.", error))
                .then()
                .doOnSuccess(result -> log.info("Periodic schedule ended."))
                .subscribe();
    }

    @Scheduled(cron = "${application.scheduler.weekly.cron}", zone = "${application.scheduler.weekly.zone}")
    public void runWeeklySchedule() {
        log.debug("Weekly schedule started.");
        Flux.fromIterable(jobSiteAlerterRegistry.getAll())
                .flatMap(JobSiteAlerter::weeklySummary)
                .onErrorContinue((error, o) -> log.error("Failed to check site while running weekly schedule.", error))
                .then()
                .doOnSuccess(result -> log.info("Weekly schedule ended."))
                .subscribe();
    }
}
