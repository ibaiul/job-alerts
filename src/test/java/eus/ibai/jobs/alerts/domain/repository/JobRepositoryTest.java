package eus.ibai.jobs.alerts.domain.repository;

import eus.ibai.jobs.alerts.AcceptanceTest;
import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.infrastructure.repository.JobEntityRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

import static eus.ibai.jobs.alerts.TestData.JOB_1_TITLE;
import static eus.ibai.jobs.alerts.TestData.JOB_SITE_1_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class JobRepositoryTest extends AcceptanceTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobEntityRepository jobEntityRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void should_update_job_when_synchronising_and_already_exists() {
        Job job = new Job(JOB_1_TITLE, "oldUrl");
        JobSiteSummary jobSiteSummary = new JobSiteSummary(JOB_SITE_1_NAME, "siteUrl", List.of(job));
        StepVerifier.create(jobRepository.sync(jobSiteSummary))
                .verifyComplete();

        Job updatedJob = new Job(JOB_1_TITLE, "newUrl");
        jobSiteSummary = new JobSiteSummary(JOB_SITE_1_NAME, "siteUrl", List.of(updatedJob));
        StepVerifier.create(jobRepository.sync(jobSiteSummary))
                .verifyComplete();

        StepVerifier.create(jobRepository.getEnabledJobsBySiteName(JOB_SITE_1_NAME).collectList())
                .expectNext(List.of(updatedJob))
                .verifyComplete();
    }

    @Test
    void should_delete_job_when_synchronising_and_job_not_present_in_job_site() {
        String siteUrl = "siteUrl";
        String deletedJobTitle = "Job 1";
        String deletedJobUrl = "url1";
        Job job1 = new Job(deletedJobTitle, deletedJobUrl);
        Job job2 = new Job("Job 2", "url2");
        JobSiteSummary jobSiteSummary = new JobSiteSummary(JOB_SITE_1_NAME, siteUrl, List.of(job1, job2));
        StepVerifier.create(jobRepository.sync(jobSiteSummary))
                .verifyComplete();

        Job job3 = new Job("Job 3", "url3");
        List<Job> expectedJobs = List.of(job2, job3);
        jobSiteSummary = new JobSiteSummary(JOB_SITE_1_NAME, siteUrl, expectedJobs);
        StepVerifier.create(jobRepository.sync(jobSiteSummary))
                .verifyComplete();

        StepVerifier.create(jobEntityRepository.findByTitle(deletedJobTitle))
                .expectNextMatches(jobEntity -> !jobEntity.isEnabled())
                .verifyComplete();
        StepVerifier.create(jobRepository.getEnabledJobsBySiteName(JOB_SITE_1_NAME).collectList())
                .expectNext(expectedJobs)
                .verifyComplete();
    }

    @Test
    void should_record_active_jobs_metric_when_synchronising() {
        Job job1 = new Job(JOB_1_TITLE, "url");
        Job job2 = new Job("Job 2", "url2");
        JobSiteSummary jobSiteSummary = new JobSiteSummary(JOB_SITE_1_NAME, "siteUrl", List.of(job1, job2));

        StepVerifier.create(jobRepository.sync(jobSiteSummary))
                .verifyComplete();

        Gauge activeJobsGauge = meterRegistry.find("jobs.active").tag("site_name", JOB_SITE_1_NAME).gauge();
        assertThat(activeJobsGauge, notNullValue());
        assertThat(activeJobsGauge.value(), equalTo(2.0d));
    }
}