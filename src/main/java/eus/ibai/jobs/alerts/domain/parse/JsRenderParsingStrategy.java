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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class JsRenderParsingStrategy implements JobParsingStrategy {

    public static final String TYPE = "jsRender";

    private static final Pattern STEP_LOAD_PATTERN = Pattern.compile("load:(id|class|title)=([a-zA-Z0-9_\\- ]+)");

    private static final Pattern STEP_CLICK_PATTERN = Pattern.compile("click:(id|class)=([a-zA-Z0-9_\\- ]+)(\\[(\\d+(,\\d+)*)\\])?");

    private final List<JsStep> initialSteps;

    private final String parsingSteps;

    private final int waitSeconds;

    private final WebDriverFactory webDriverFactory;

    JsRenderParsingStrategy(List<String> initialSteps, String parsingSteps, int waitSeconds, WebDriverFactory webDriverFactory) {
        this.initialSteps = parseSteps(initialSteps);
        this.parsingSteps = parsingSteps;
        this.waitSeconds = waitSeconds;
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public Flux<Job> parseJobs(String siteUrl) {
        return Mono.just(webDriverFactory.firefoxRemoteWebDriver())
                .flatMap(webDriver -> renderPage(webDriver, siteUrl)
                        .doOnNext(html -> log.info("Rendered HTML response from {}: {}", siteUrl, html))
                        .timeout(Duration.ofSeconds(waitSeconds))
                        .doFinally(signalType -> webDriver.quit()))
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
        waitUntil(webDriver, step.elementType(), step.elementValue());
        if (step.action() == JsAction.CLICK) {
            click(webDriver, step.elementType(), step.elementValue(), step.extra());
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
        fluentWait.withTimeout(Duration.ofSeconds(waitSeconds));
        boolean clickSucceeded = fluentWait.until(elem -> {
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

        if (!clickSucceeded) {
            throw new IllegalStateException("Failed to click element %s".formatted(element));
        }
    }

    private void waitUntil(WebDriver webDriver, JsElementType elementType, String elementValue) {
        ExpectedCondition<Boolean> expectedCondition = switch (elementType) {
            case ID -> expectElementExists(By.id(elementValue));
            case CLASS -> expectElementExists(By.className(elementValue));
            case TITLE -> ExpectedConditions.titleContains(elementValue);
        };

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(waitSeconds));
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
                case LOAD -> {
                    Matcher matcher = STEP_LOAD_PATTERN.matcher(initialStep);
                    if (matcher.matches()) {
                        yield new JsStep(jsAction, JsElementType.valueOf(matcher.group(1).toUpperCase()), matcher.group(2));
                    }
                    yield null;
                }
                case CLICK -> {
                    Matcher matcher = STEP_CLICK_PATTERN.matcher(initialStep);
                    if (matcher.matches()) {
                        yield new JsStep(jsAction, JsElementType.valueOf(matcher.group(1).toUpperCase()), matcher.group(2), matcher.group(3));
                    }
                    yield null;
                }
            };
            if (jsStep == null) {
                throw new IllegalArgumentException("Could not parse step: " + initialStep);
            }
            jsSteps.add(jsStep);
        });
        return jsSteps;
    }
}
