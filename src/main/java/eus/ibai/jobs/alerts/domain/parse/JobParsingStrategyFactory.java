package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.infrastructure.jsoup.BasicHttpClient;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

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
                String waitUntil = parsingStrategyDefinition.get("waitUntil").toString();
                int waitSeconds = (int) parsingStrategyDefinition.getOrDefault("waitSeconds", 5);
                yield new JsRenderParsingStrategy(steps, waitUntil, waitSeconds, webDriverFactory);
            }
            default -> throw new IllegalArgumentException("Unknown parsing strategy " + type);
        };
    }
}
