package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@Testcontainers
class JsRenderParsingStrategyTest {

    private static final String JOB_SITE_URL_FORMAT = "http://%s:8080/job-site-1";

    private final Network containerNetwork = Network.newNetwork();

    @Container
    private final WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("jobSite1", "job-site-1.json")
            .withNetwork(containerNetwork)
            .withNetworkMode(containerNetwork.getId())
            .withNetworkAliases("wiremock");

    @Container
    private final BrowserWebDriverContainer<?> firefox = new BrowserWebDriverContainer<>()
            .withCapabilities(new FirefoxOptions())
            .withNetwork(containerNetwork);

    @Test
    void should_wait_until_element_with_class_renders() {
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy("div,ul,li.foo,a", "class=foo", 5,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs("http://wiremock:8080/job-site-1")
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_wait_until_element_with_id_renders() {
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy("div,ul,li.foo,a", "id=foo", 2,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_wait_until_title_renders() {
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy("div,ul,li.foo,a", "title=Rendered Title", 2,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_timeout_when_wait_until_condition_is_not_met() {
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy("div,ul,li.foo,a", "class=nonExistingClass", 1,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectError(TimeoutException.class)
                .verify();
    }

    @Test
    void should_fail_when_wait_until_condition_is_malformed() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new JsRenderParsingStrategy("div,ul,li.foo,a", "foo=bar", 2,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444))));
    }
}
