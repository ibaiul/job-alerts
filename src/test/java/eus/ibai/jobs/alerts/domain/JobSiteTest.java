package eus.ibai.jobs.alerts.domain;

import eus.ibai.jobs.alerts.domain.parse.JobParsingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSiteTest {

    @Mock
    private JobParsingStrategy jobParsingStrategy;

    private JobSite jobSite;

    @BeforeEach
    void beforeEach() {
        jobSite = new JobSite("siteName", "siteUrl", jobParsingStrategy, "jpSteps");
    }

    @Test
    void should_return_job_summary() {
        List<Job> expectedJobs = List.of(new Job("jobTitle", "jobUrl"));
        when(jobParsingStrategy.parseJobs(jobSite.getUrl(), jobSite.getParsingStrategySteps())).thenReturn(Flux.fromIterable(expectedJobs));
        JobSiteSummary expectedSummary = new JobSiteSummary(jobSite.getName(), jobSite.getUrl(), expectedJobs);

        StepVerifier.create(jobSite.getSummary())
                .expectNext(expectedSummary)
                .verifyComplete();
    }

    @Test
    void should_discard_duplicate_jobs_when_summary_contains_jobs_with_same_title() {
        String jobTitle = "jobTitle";
        Job job = new Job(jobTitle, "jobUrl1");
        Job duplicatedJob = new Job(jobTitle, "jobUrl2");
        List<Job> jobs = List.of(job, duplicatedJob);
        when(jobParsingStrategy.parseJobs(jobSite.getUrl(), jobSite.getParsingStrategySteps())).thenReturn(Flux.fromIterable(jobs));
        List<Job> expectedJobs = List.of(job);
        JobSiteSummary expectedSummary = new JobSiteSummary(jobSite.getName(), jobSite.getUrl(), expectedJobs);

        StepVerifier.create(jobSite.getSummary())
                .expectNext(expectedSummary)
                .verifyComplete();
    }
}