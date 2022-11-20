package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.AcceptanceTest;
import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerterRegistry;
import eus.ibai.jobs.alerts.domain.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

import static eus.ibai.jobs.alerts.TestData.*;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
class MainSchedulerTest extends AcceptanceTest {

    @Autowired
    private MainScheduler mainScheduler;

    @Autowired
    private JobSiteAlerterRegistry alerterRegistry;

    @Autowired
    private JobRepository jobRepository;

    @Test
    void should_persist_jobs_when_checking_site_periodically() {
        mainScheduler.runPeriodicSchedule();

        Job expectedJob1 = new Job(JOB_1_TITLE, format(JOB_1_URL_FORMAT, wiremockBaseUrl()));
        Job expectedJob2 = new Job(JOB_2_TITLE, format(JOB_2_URL_FORMAT, wiremockBaseUrl()));
        await().atMost(3, SECONDS).untilAsserted(() -> StepVerifier.create(jobRepository.getEnabledJobsBySiteName(JOB_SITE_1_NAME).sort(comparing(Job::getTitle)))
                .expectNext(expectedJob1)
                .expectNext(expectedJob2)
                .verifyComplete());
    }

    @Test
    void should_notify_when_checking_site_periodically_and_site_has_changed() {
        mainScheduler.runPeriodicSchedule();

        await().atMost(3, SECONDS).ignoreExceptions().untilAsserted(() -> {
            verifyTelegramMessageSent(VALID_CHAT_ID, 1);
            verifyEmailSent("job-alert-recipient@localhost", "Job Site Updated: \"JobSite1\"");
        });
    }

    @Test
    void should_record_active_jobs_when_checking_site_periodically() {
        mainScheduler.runPeriodicSchedule();

        verifyActiveJobsMetricRecorded(JOB_SITE_1_NAME, 2);
    }

    @Test
    void should_not_notify_when_checking_site_periodically_and_site_has_not_changed() {
        Job job1 = new Job(JOB_1_TITLE, format(JOB_1_URL_FORMAT, wiremockBaseUrl()));
        Job job2 = new Job(JOB_2_TITLE, format(JOB_2_URL_FORMAT, wiremockBaseUrl()));
        JobSiteSummary summary = new JobSiteSummary(JOB_SITE_1_NAME, format(JOB_SITE_1_URL_FORMAT, wiremockBaseUrl()), List.of(job1, job2));
        StepVerifier.create(jobRepository.sync(summary))
                .verifyComplete();

        mainScheduler.runPeriodicSchedule();

        await().during(3, SECONDS).untilAsserted(() -> {
            verifyNoTelegramMessageSent();
            verifyNoEmailSent();
        });
    }

    @Test
    void should_notify_when_checking_site_weekly() {
        mainScheduler.runWeeklySchedule();

        await().atMost(3, SECONDS).ignoreExceptions().untilAsserted(() -> {
            verifyTelegramMessageSent(VALID_CHAT_ID, 1);
            verifyEmailSent("job-alert-recipient@localhost", "Job Site Weekly Summary: \"JobSite1\"");
        });
    }
}