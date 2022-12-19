package eus.ibai.jobs.alerts.infrastructure.jsoup;

import eus.ibai.jobs.alerts.AcceptanceTest;
import eus.ibai.jobs.alerts.domain.parse.ParsingException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.test.StepVerifier;

import static eus.ibai.jobs.alerts.TestData.*;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

class JsoupClientTest extends AcceptanceTest {

    @Autowired
    private JsoupClient jsoupClient;

    @Value("${application.site.read-timeout}")
    private int readTimeout;

    @Test
    void should_retrieve_dom_when_job_site_exists() {
        StepVerifier.create(jsoupClient.parse(format(JOB_SITE_1_URL_FORMAT, wiremockBaseUrl())))
                .expectNextMatches(document -> !document.select("ul.menu_pag").isEmpty())
                .verifyComplete();
        verifyJobSiteRequestMetricRecorded(JOB_SITE_1_PATH, 200, 1L);
    }

    @Test
    void should_throw_exception_when_job_site_cannot_be_reached() {
        StepVerifier.create(jsoupClient.parse(format(NON_EXISTENT_JOB_SITE_URL_FORMAT, wiremockBaseUrl())))
                .expectNextCount(0)
                .verifyErrorSatisfies(error -> {
                    assertThat(error, instanceOf(ParsingException.class));
                    assertThat(error.getCause(), nullValue());
                });
        verifyJobSiteRequestMetricRecorded(NON_EXISTENT_JOB_SITE_PATH, 404, 1L);
    }

    @Test
    void should_map_exception_when_unexpected_error_occurs() {
        StepVerifier.create(jsoupClient.parse("http://localhost:65536/port-out-of-range"))
                .expectNextCount(0)
                .verifyErrorSatisfies(error -> {
                    assertThat(error, instanceOf(ParsingException.class));
                    assertThat(error.getCause(), instanceOf(WebClientRequestException.class));
                });
    }

    @Test
    void should_timeout_when_request_takes_too_long() {
        int delay = (readTimeout + 1) * 1000;
        stubSlowJobSite(delay);

        StepVerifier.create(jsoupClient.parse(format(JOB_SITE_TIMEOUT_URL_FORMAT, wiremockBaseUrl())))
                .expectNextCount(0)
                .verifyErrorSatisfies(error -> {
                    assertThat(error, instanceOf(ParsingException.class));
                    assertThat(error.getCause(), instanceOf(WebClientRequestException.class));
                    assertThat(error.getCause().getCause(), instanceOf(ReadTimeoutException.class));
                });
    }
}