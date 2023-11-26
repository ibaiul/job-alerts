package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JsRenderParsingStrategy implements JobParsingStrategy {

    public static final String TYPE = "jsRender";

    private static final Pattern WAIT_UNTIL_PATTERN = Pattern.compile("(id|class|title)=([a-zA-Z0-9_\\- ]+)");

    private final String steps;

    private final ExpectedCondition<Boolean> expectedCondition;

    private final int waitSeconds;

    private final WebDriverFactory webDriverFactory;

    public JsRenderParsingStrategy(String steps, String waitUntil, int waitSeconds, WebDriverFactory webDriverFactory) {
        this.steps = steps;
        Matcher matcher = WAIT_UNTIL_PATTERN.matcher(waitUntil);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid waitUntil format: " + waitUntil);
        }
        String waitUntilContains = matcher.group(1);
        String waitUntilValue = matcher.group(2);
        this.expectedCondition = switch (waitUntilContains) {
            case "id" -> ExpectedConditions.elementSelectionStateToBe(By.id(waitUntilValue), false);
            case "class" -> ExpectedConditions.elementSelectionStateToBe(By.className(waitUntilValue), false);
            case "title" -> ExpectedConditions.titleContains(waitUntilValue);
            default -> throw new IllegalStateException("Unexpected value: " + waitUntilContains);
        };
        this.waitSeconds = waitSeconds;
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public Flux<Job> parseJobs(String siteUrl) {
        return Mono.just(webDriverFactory.firefoxRemoteWebDriver())
                .flatMap(webDriver -> renderPage(webDriver, siteUrl)
                        .doOnNext(html -> log.trace("Rendered HTML response from {}: {}", siteUrl, html))
                        .doFinally(signalType -> webDriver.close()))
                .flatMapMany(html -> new JsoupJobParser().parseJobs(html, steps, siteUrl));
    }

    private Mono<String> renderPage(WebDriver webDriver, String url) {
        return Mono.fromCallable(() -> {
            webDriver.get(url);
            webDriver.manage().deleteAllCookies();
            webDriver.manage().addCookie(new Cookie("cookieconsent_status", "deny"));
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(waitSeconds));
            wait.until(expectedCondition);
            return webDriver.findElement(By.tagName("html")).getAttribute("innerHTML");
        });
    }
}
