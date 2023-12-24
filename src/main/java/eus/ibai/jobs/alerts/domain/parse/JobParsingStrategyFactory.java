package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.infrastructure.jsoup.BasicHttpClient;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobParsingStrategyFactory {

    private final BasicHttpClient basicHttpClient;

    private final WebDriverFactory webDriverFactory;

    public JobParsingStrategy getStrategy(Map<String, Object> parsingStrategyDefinition) {
        String type = parsingStrategyDefinition.get("type").toString();
        String steps = parsingStrategyDefinition.get("steps").toString();
        return switch (type) {
            case BasicHtmlParsingStrategy.TYPE -> new BasicHtmlParsingStrategy(steps, basicHttpClient, new JsoupJobParser());
            case JsRenderParsingStrategy.TYPE -> {
                List<String> initialSteps = Optional.ofNullable(parsingStrategyDefinition.get("initialSteps"))
                        .map(o -> (List<String>) o)
                        .orElse(Collections.emptyList());
                int waitSeconds = (int) parsingStrategyDefinition.getOrDefault("waitSeconds", 5);
                yield new JsRenderParsingStrategy(initialSteps, steps, waitSeconds, webDriverFactory);
            }
            default -> throw new IllegalArgumentException("Unknown parsing strategy " + type);
        };
    }
}
