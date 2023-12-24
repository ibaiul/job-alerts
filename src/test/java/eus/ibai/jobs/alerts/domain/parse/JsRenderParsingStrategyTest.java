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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@Testcontainers
class JsRenderParsingStrategyTest {

    private static final String JOB_SITE_URL_FORMAT = "http://%s:8080/job-site-1";

    private final Network containerNetwork = Network.newNetwork();

    @Container
    private final WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromJSON("jobSite1", jobSiteDefinition())
            .withNetwork(containerNetwork)
            .withNetworkMode(containerNetwork.getId())
            .withNetworkAliases("wiremock");

    @Container
    private final BrowserWebDriverContainer<?> firefox = new BrowserWebDriverContainer<>()
            .withCapabilities(new FirefoxOptions())
            .withNetwork(containerNetwork);

    @Test
    void should_wait_until_element_with_class_renders() {
        List<String> initialSteps = List.of("load:class=dynamic-class");
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy(initialSteps, "div,ul,li.job-item,a", 5,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs("http://wiremock:8080/job-site-1")
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_wait_until_element_with_id_renders() {
        List<String> initialSteps = List.of("load:id=dynamic-id");
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy(initialSteps, "div,ul,li.job-item,a", 2,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_wait_until_title_renders() {
        List<String> initialSteps = List.of("load:title=Dynamic Title");
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy(initialSteps, "div,ul,li.job-item,a", 2,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle2", "https://job2.com"))
                .verifyComplete();
    }

    @Test
    void should_apply_initial_steps() {
        List<String> initialSteps = List.of("click:id=menu-item", "click:class=filter-item[0,1]","load:class=dynamic-job-item");
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy(initialSteps, "div#dynamic-id,ul,li.job-item,a", 5,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectNext(new Job("jobTitle1", "https://job1.com"))
                .expectNext(new Job("jobTitle3", "https://job3.com"))
                .verifyComplete();
    }

    @Test
    void should_timeout_when_wait_until_condition_is_not_met() {
        List<String> initialSteps = List.of("load:class=nonExistingClass");
        JsRenderParsingStrategy parsingStrategy = new JsRenderParsingStrategy(initialSteps, "div,ul,li.foo,a", 1,
                new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        parsingStrategy.parseJobs(JOB_SITE_URL_FORMAT.formatted(wiremockServer.getNetworkAliases().get(0)))
                .as(StepVerifier::create)
                .expectError(TimeoutException.class)
                .verify();
    }

    @Test
    void should_fail_when_wait_until_condition_is_malformed() {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            List<String> initialSteps = List.of("foo=bar");
            new JsRenderParsingStrategy(initialSteps, "div,ul,li.foo,a", 2, new WebDriverFactory("http://localhost:" + firefox.getMappedPort(4444)));
        });
    }

    private String jobSiteDefinition() {
        return """
                {
                  "request": {
                    "method": "GET",
                    "url": "/job-site-1",
                    "headers": {
                    }
                  },
                  "response": {
                    "status": 200,
                    "body": "%s",
                    "headers": {
                      "Content-Type": "text/html"
                    }
                  }
                }
                """.formatted(jobSiteHtml());
    }

    private String jobSiteHtml() {
        return """
                <html>
                    <head>
                        <title>Original title</title>
                    </head>
                    <body>
                        <div>
                            <ul>
                                <li class='job-item'>
                                    <a href='https://job1.com'>jobTitle1</a>
                                </li>
                                <li class='job-item'>
                                    <a href='https://job2.com'>jobTitle2</a>
                                </li>
                            </ul>
                        </div>
                        <div>
                            <ul>
                                <li>
                                    <a href='https://foo.bar'>notAJob</a>
                                </li>
                            </ul>
                        </div>
                        <div id='high-div' style='height: 10000px;'>
                        </div>
                        <div id='bottom-div'>
                        </div>
                        <script>
                            document.title = 'Dynamic Title';
                            document.getElementsByTagName('div')[0].setAttribute('id', 'dynamic-id');
                            document.getElementsByTagName('ul')[0].setAttribute('class', 'dynamic-class');
                        </script>
                        <script>
                            var menuDiv = document.createElement('div');
                            menuDiv.setAttribute('onclick', 'showFilter()');
                            menuDiv.setAttribute('id', 'menu-item');
                            var text = document.createTextNode('Clickable menu');
                            menuDiv.appendChild(text);
                            var bottomDiv = document.getElementById('bottom-div');
                            bottomDiv.appendChild(menuDiv);
                            
                            function showFilter() {
                                var text1 = document.createTextNode('Clickable filter');
                                var text2 = document.createTextNode('Clickable filter');
                                
                                var filterDiv1 = document.createElement('div');
                                filterDiv1.setAttribute('onclick', 'applyFilter1()');
                                filterDiv1.setAttribute('class', 'filter-item');
                                filterDiv1.appendChild(text1);
                                
                                var filterDiv2 = document.createElement('div');
                                filterDiv2.setAttribute('onclick', 'applyFilter2()');
                                filterDiv2.setAttribute('class', 'filter-item');
                                filterDiv2.appendChild(text2);
                                
                                var bottomDiv = document.getElementById('bottom-div');
                                bottomDiv.appendChild(filterDiv1);
                                bottomDiv.appendChild(filterDiv2);
                            }
                            
                            function applyFilter1() {
                                var element = document.getElementsByClassName('job-item')[1];
                                element.remove();
                            }
                            
                            function applyFilter2() {
                                var jobLi = document.createElement('li');
                                jobLi.setAttribute('class', 'job-item dynamic-job-item');
                                var jobAnchor = document.createElement('a');
                                jobAnchor.setAttribute('href', 'https://job3.com');
                                var text = document.createTextNode('jobTitle3');
                                jobAnchor.appendChild(text);
                                jobLi.appendChild(jobAnchor);
                                var jobUl = document.getElementsByClassName('dynamic-class')[0];
                                jobUl.appendChild(jobLi);
                            }
                        </script>
                    </body>
                </html>
                """.replaceAll("\\n", "");
    }
}
