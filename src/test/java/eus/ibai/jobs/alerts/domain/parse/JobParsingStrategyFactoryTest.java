package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.infrastructure.jsoup.BasicHttpClient;
import eus.ibai.jobs.alerts.infrastructure.selenium.WebDriverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static eus.ibai.jobs.alerts.TestData.initialSteps;
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
        Map<String, Object> parsingStrategySettings = Map.of("type", "jsRender", "initialSteps", initialSteps(List.of("load:class=foo")), "steps", "steps", "waitSeconds", 2);

        JobParsingStrategy jobParsingStrategy = parsingStrategyFactory.getStrategy(parsingStrategySettings);

        assertThat(jobParsingStrategy).isExactlyInstanceOf(JsRenderParsingStrategy.class);
    }

    @Test
    void should_fail_to_create_parsing_strategy_when_type_is_invalid() {
        Map<String, Object> parsingStrategySettings = Map.of("type", "invalidType", "steps", "steps");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parsingStrategyFactory.getStrategy(parsingStrategySettings));
        assertThat(exception).hasMessageContaining("Unknown parsing strategy invalidType");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "wait:class=foo", "load:attr=foo", "click:attr=foo"})
    void should_fail_to_create_js_render_parsing_strategy_when_initial_steps_are_invalid(String initialSteps) {
        Map<String, Object> parsingStrategySettings = Map.of("type", "jsRender", "initialSteps", initialSteps(List.of(initialSteps)), "steps", "steps");

        assertThrows(IllegalArgumentException.class, () -> parsingStrategyFactory.getStrategy(parsingStrategySettings));
    }
}