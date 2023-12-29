package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class JsRenderParsingStrategy implements JobParsingStrategy {

    public static final String TYPE = "jsRender";

    private static final Pattern STEP_WAIT_PATTERN = Pattern.compile("wait:(\\d+)");

    private static final Pattern STEP_LOAD_PATTERN = Pattern.compile("load:(id|class|title)=([a-zA-Z0-9_\\- ]+)");

    private static final Pattern STEP_CLICK_PATTERN = Pattern.compile("click:(id|class)=([a-zA-Z0-9_\\- ]+)(\\[(\\d+(,\\d+)*)\\])?");

    private final List<JsStep> initialSteps;

    private final String parsingSteps;

    private final int stepTimeout;

    private final int parseTimeout;

    private final WebDriverFactory webDriverFactory;

    JsRenderParsingStrategy(List<String> initialSteps, String parsingSteps, int stepTimeout, int parseTimeout, WebDriverFactory webDriverFactory) {
        this.initialSteps = parseSteps(initialSteps);
        this.parsingSteps = parsingSteps;
        this.stepTimeout = stepTimeout;
        this.parseTimeout = parseTimeout;
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public Flux<Job> parseJobs(String siteUrl) {
        return webDriverFactory.firefoxRemoteWebDriver()
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(webDriver -> renderPage(webDriver, siteUrl)
                        .doOnNext(html -> log.trace("Rendered HTML response from {}: {}", siteUrl, html))
                        .timeout(Duration.ofSeconds(parseTimeout))
                        .doFinally(signalType -> webDriver.quit()))
                .publishOn(Schedulers.parallel())
                .flatMapMany(html -> new JsoupJobParser().parseJobs(html, parsingSteps, siteUrl));
    }

    private Mono<String> renderPage(WebDriver webDriver, String url) {
        return Mono.fromCallable(() -> {
            webDriver.get(url);
            webDriver.manage().deleteAllCookies();
            webDriver.manage().addCookie(new Cookie("cookieconsent_status", "deny"));
            initialSteps.forEach(step -> applyStep(webDriver, step));
            return webDriver.findElement(By.tagName("html")).getAttribute("innerHTML");
        });
    }

    private void applyStep(WebDriver webDriver, JsStep step) {
        JsAction action = step.action();
        switch (action) {
            case WAIT -> {
                int wait = Integer.parseInt(step.extra()) * 1000;
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Failed to apply wait step.");
                }
            }
            case LOAD -> waitUntil(webDriver, step.elementType(), step.elementValue());
            case CLICK -> {
                waitUntil(webDriver, step.elementType(), step.elementValue());
                click(webDriver, step.elementType(), step.elementValue(), step.extra());
            }
        }
    }

    private void click(WebDriver webDriver, JsElementType elementType, String elementValue, String elementIndexes) {
        List<Integer> indexes = List.of(0);
        if (elementIndexes != null) {
            indexes = Stream.of(elementIndexes.replaceAll("[\\[\\]]", "").split(","))
                    .map(Integer::parseInt)
                    .toList();
        }
        By elementCriteria = switch (elementType) {
            case ID -> By.id(elementValue);
            case CLASS -> By.className(elementValue);
            default -> throw new IllegalStateException("Unexpected click element type: " + elementType);
        };

        List<WebElement> elements = webDriver.findElements(elementCriteria);
        indexes.forEach(i -> clickElement(webDriver, elements.get(i)));
    }

    private void clickElement(WebDriver webDriver, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].scrollIntoView(true);", element); // scrollToElement does not seem to work with Firefox

        FluentWait<WebElement> fluentWait = new FluentWait<>(element);
        fluentWait.withTimeout(Duration.ofSeconds(stepTimeout));
        fluentWait.until(elem -> {
            try {
                new Actions(webDriver)
                        .moveToElement(elem)
                        .click()
                        .perform();
            } catch (Exception e) {
                log.trace("Failed to click element %s, will retry. Exception: %s. Retrying ...".formatted(element, e.getClass()));
                return false;
            }

            return true;
        });
    }

    private void waitUntil(WebDriver webDriver, JsElementType elementType, String elementValue) {
        ExpectedCondition<Boolean> expectedCondition = switch (elementType) {
            case ID -> expectElementExists(By.id(elementValue));
            case CLASS -> expectElementExists(By.className(elementValue));
            case TITLE -> ExpectedConditions.titleContains(elementValue);
        };

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(stepTimeout));
        wait.until(expectedCondition);
    }

    private ExpectedCondition<Boolean> expectElementExists(By locator) {
        return webDriver -> webDriver.findElement(locator) != null;
    }

    private List<JsStep> parseSteps(List<String> initialSteps) {
        List<JsStep> jsSteps = new ArrayList<>(initialSteps.size());
        initialSteps.forEach(initialStep -> {
            JsAction jsAction = JsAction.valueOf(initialStep.split(":")[0].toUpperCase());
            JsStep jsStep = switch (jsAction) {
                case WAIT -> {
                    Matcher matcher = STEP_WAIT_PATTERN.matcher(initialStep);
                    if (matcher.matches()) {
                        yield new JsStep(jsAction, null, null, matcher.group(1));
                    }
                    throw new IllegalArgumentException("Malformed WAIT step: %s".formatted(initialStep));
                }
                case LOAD -> {
                    Matcher matcher = STEP_LOAD_PATTERN.matcher(initialStep);
                    if (matcher.matches()) {
                        yield new JsStep(jsAction, JsElementType.valueOf(matcher.group(1).toUpperCase()), matcher.group(2));
                    }
                    throw new IllegalArgumentException("Malformed LOAD step: %s".formatted(initialStep));
                }
                case CLICK -> {
                    Matcher matcher = STEP_CLICK_PATTERN.matcher(initialStep);
                    if (matcher.matches()) {
                        yield new JsStep(jsAction, JsElementType.valueOf(matcher.group(1).toUpperCase()), matcher.group(2), matcher.group(3));
                    }
                    throw new IllegalArgumentException("Malformed CLICK step: %s".formatted(initialStep));
                }
            };
            jsSteps.add(jsStep);
        });
        return jsSteps;
    }
}
