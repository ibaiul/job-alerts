package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.test.StepVerifier;

import static eus.ibai.jobs.alerts.TestData.*;

class JsoupJobParserTest {

    private final JsoupJobParser parser = new JsoupJobParser();

    @Test
    void should_parse_jobs_providing_element_class_in_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String html = htmlWithNestedElementJobs();
        String steps = "div,ul,li.class1,a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_providing_element_id_in_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String html = htmlWithNestedElementJobs();
        String steps = "div#id,ul,li,a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_providing_the_minimum_identifiable_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String html = htmlWithNestedElementJobs();
        String steps = "div#id,a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_providing_element_index_in_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String html = htmlWithNestedElementJobs();
        String steps = "div,ul,li.class1[1],a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_fail_to_parse_jobs_when_providing_unexisting_element_index_in_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String html = htmlWithNestedElementJobs();
        String steps = "div,ul,li.class1[2],a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .verifyError(ParsingException.class);
    }

    @Test
    void should_parse_jobs_with_absolute_urls() {
        String siteUrl = "https://job-portal.com/jobs";
        String jobTitle = "jobTitle";
        String jobUrl = "https://another-job-portal.com/job1";
        String html = htmlWithSingleAnchorJob(jobTitle, jobUrl);
        String steps = "a";
        Job expectedJob = new Job(jobTitle, jobUrl);

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_with_absolute_paths() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String jobTitle = "jobTitle";
        String absolutePath = "/foo/bar/job1";
        String jobUrl = "https://job-portal.com" + absolutePath;
        String html = htmlWithSingleAnchorJob(jobTitle, absolutePath);
        Job expectedJob = new Job(jobTitle, jobUrl);
        String steps = "a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"foo/job1", "./foo", "../2023/index.html"})
    void should_drop_job_url_when_parsing_jobs_with_relative_paths(String jobHref) {
        String siteUrl = "https://job-portal.com/jobs/2023/index.html";
        String jobTitle = "jobTitle";
        String html = htmlWithSingleAnchorJob(jobTitle, jobHref);
        Job expectedJob = new Job(jobTitle, null);
        String steps = "a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    @Test
    void should_drop_job_url_when_parsing_jobs_with_no_href_attribute() {
        String siteUrl = "https://job-portal.com/jobs/2023/index.html";
        String jobTitle = "jobTitle";
        String html = htmlWithSingleAnchorJobAndNoHrefAttribute(jobTitle);
        Job expectedJob = new Job(jobTitle, null);
        String steps = "a";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    @Test
    void should_drop_job_url_when_parsing_jobs_with_anchor_element() {
        String siteUrl = "https://job-portal.com/jobs/2023/index.html";
        String jobTitle = "jobTitle";
        String html = htmlWithSingleNonAnchorJob(jobTitle);
        Job expectedJob = new Job(jobTitle, null);
        String steps = "span.class1";

        parser.parseJobs(html, steps, siteUrl)
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }
}
