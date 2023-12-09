package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.jsoup.BasicHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static eus.ibai.jobs.alerts.TestData.htmlWithNestedElementJobs;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicHtmlParsingStrategyTest {

    @Mock
    private BasicHttpClient basicHttpClient;

    @Mock
    private JsoupJobParser jsoupJobParser;

    @Test
    void should_delegate_parsing_to_jsoup_job_parser() {
        String siteUrl = "https://job-portal.com/jobs/2023";
        String html = htmlWithNestedElementJobs();
        Job expectedJob1 = new Job("jobTitle1", "https://job1.com");
        Job expectedJob2 = new Job("jobTitle2", "https://job2.com");
        String steps = "div,ul,li.class1,a";
        when(basicHttpClient.parse(siteUrl)).thenReturn(Mono.just(html));
        when(jsoupJobParser.parseJobs(html, steps, siteUrl)).thenReturn(Flux.just(expectedJob1, expectedJob2));
        BasicHtmlParsingStrategy parsingStrategy = new BasicHtmlParsingStrategy(steps, basicHttpClient, jsoupJobParser);

        parsingStrategy.parseJobs(siteUrl)
                .as(StepVerifier::create)
                .expectNext(expectedJob1)
                .expectNext(expectedJob2)
                .verifyComplete();
    }
}
