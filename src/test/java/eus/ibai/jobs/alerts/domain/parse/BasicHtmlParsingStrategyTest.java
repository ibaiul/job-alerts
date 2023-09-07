package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.jsoup.JsoupClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicHtmlParsingStrategyTest {

    @Mock
    private JsoupClient jsoupClient;

    @InjectMocks
    private BasicHtmlParsingStrategy parsingStrategy;

    @Test
    void should_parse_jobs_providing_element_class_in_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        Document document = documentWithNestedElementJobs(siteUrl);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob1 = new Job("jobTitle1", "https://job1.com");
        Job expectedJob2 = new Job("jobTitle2", "https://job2.com");

        parsingStrategy.parseJobs(siteUrl, "div,ul,li.class1,a")
                .as(StepVerifier::create)
                .expectNext(expectedJob1)
                .expectNext(expectedJob2)
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_providing_element_id_in_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        Document document = documentWithNestedElementJobs(siteUrl);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob1 = new Job("jobTitle1", "https://job1.com");
        Job expectedJob2 = new Job("jobTitle2", "https://job2.com");

        parsingStrategy.parseJobs(siteUrl, "div#id,ul,li,a")
                .as(StepVerifier::create)
                .expectNext(expectedJob1)
                .expectNext(expectedJob2)
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_providing_the_minimum_identifiable_steps() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        Document document = documentWithNestedElementJobs(siteUrl);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob1 = new Job("jobTitle1", "https://job1.com");
        Job expectedJob2 = new Job("jobTitle2", "https://job2.com");

        parsingStrategy.parseJobs(siteUrl, "div#id,a")
                .as(StepVerifier::create)
                .expectNext(expectedJob1)
                .expectNext(expectedJob2)
                .verifyComplete();
    }

    @Test
    void should_parse_jobs_with_absolute_urls() {
        String siteUrl = "https://job-portal.com/jobs";
        String jobTitle = "jobTitle";
        String jobUrl = "https://another-job-portal.com/job1";
        Document document = documentWithSingleAnchorJob(siteUrl, jobTitle, jobUrl);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob = new Job(jobTitle, jobUrl);

        parsingStrategy.parseJobs(siteUrl, "a")
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
        Document document = documentWithSingleAnchorJob(siteUrl, jobTitle, absolutePath);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob = new Job(jobTitle, jobUrl);

        parsingStrategy.parseJobs(siteUrl, "a")
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"foo/job1", "./foo", "../2023/index.html"})
    void should_drop_job_url_when_parsing_jobs_with_relative_paths(String jobHref) {
        String siteUrl = "https://job-portal.com/jobs/2023/index.html";
        String jobTitle = "jobTitle";
        Document document = documentWithSingleAnchorJob(siteUrl, jobTitle, jobHref);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob = new Job(jobTitle, null);

        parsingStrategy.parseJobs(siteUrl, "a")
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    @Test
    void should_drop_job_url_when_parsing_jobs_with_no_href_attribute() {
        String siteUrl = "https://job-portal.com/jobs/2023/index.html";
        String jobTitle = "jobTitle";
        Document document = documentWithSingleAnchorJobAndNoHrefAttribute(siteUrl, jobTitle);
        when(jsoupClient.parse(siteUrl)).thenReturn(Mono.just(document));
        Job expectedJob = new Job(jobTitle, null);

        parsingStrategy.parseJobs(siteUrl, "a")
                .as(StepVerifier::create)
                .expectNext(expectedJob)
                .verifyComplete();
    }

    private Document documentWithNestedElementJobs(String siteUrl) {
        Document document = Document.createShell(siteUrl);
        Element ul = document.body()
                .appendElement("div").id("id")
                .appendElement("ul");
        ul.appendElement("li").classNames(Set.of("class1", "class2"))
                .appendElement("a").text("jobTitle1").attr("href", "https://job1.com");
        ul.appendElement("li").classNames(Set.of("class1", "class2"))
                .appendElement("a").text("jobTitle2").attr("href", "https://job2.com");
        document.body()
                .appendElement("div")
                .appendElement("ul")
                .appendElement("li")
                .appendElement("a").text("notAJob").attr("href", "https://foo.bar");
        return document;
    }

    private Document documentWithSingleAnchorJob(String siteUrl, String jobTitle, String jobHref) {
        Document document = Document.createShell(siteUrl);
        document.body().appendElement("a").text(jobTitle).attr("href", jobHref);
        return document;
    }

    private Document documentWithSingleAnchorJobAndNoHrefAttribute(String siteUrl, String jobTitle) {
        Document document = Document.createShell(siteUrl);
        document.body().appendElement("a").text(jobTitle);
        return document;
    }
}
