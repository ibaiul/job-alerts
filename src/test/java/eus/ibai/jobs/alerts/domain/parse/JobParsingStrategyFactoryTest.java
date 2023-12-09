package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.infrastructure.jsoup.BasicHttpClient;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JobParsingStrategyFactoryTest {

    @Mock
    private BasicHttpClient basicHttpClient;

    @Mock
    private WebDriverFactory webDriverFactory;

    @InjectMocks
    JobParsingStrategyFactory parsingStrategyFactory;

    @Test
    void should_create_basic_html_parsing_strategy() {
        Map<String, Object> parsingStrategySettings = Map.of("type", "basicHtml", "steps", "steps");

        JobParsingStrategy jobParsingStrategy = parsingStrategyFactory.getStrategy(parsingStrategySettings);

        assertThat(jobParsingStrategy).isExactlyInstanceOf(BasicHtmlParsingStrategy.class);
    }

    @Test
    void should_create_js_render_parsing_strategy() {
        Map<String, Object> parsingStrategySettings = Map.of("type", "jsRender", "steps", "steps", "waitUntil", "class=foo", "waitSeconds", 2);

        JobParsingStrategy jobParsingStrategy = parsingStrategyFactory.getStrategy(parsingStrategySettings);

        assertThat(jobParsingStrategy).isExactlyInstanceOf(JsRenderParsingStrategy.class);
    }

    @Test
    void should_throw_exception_when_parsing_strategy_type_is_invalid() {
        Map<String, Object> parsingStrategySettings = Map.of("type", "invalidType", "steps", "steps");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parsingStrategyFactory.getStrategy(parsingStrategySettings));
        assertThat(exception).hasMessageContaining("Unknown parsing strategy invalidType");
    }
}