package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.infrastructure.jsoup.JsoupClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JobParserFactoryTest {

    @Test
    void should_throw_exception_when_parsing_strategy_type_is_invalid() {
        List<JobParsingStrategy> availableStrategies = List.of(new BasicHtmlParsingStrategy(new JsoupClient()));
        JobParsingStrategyFactory parsingStrategyFactory = new JobParsingStrategyFactory(availableStrategies);

        assertThrows(IllegalArgumentException.class, () -> parsingStrategyFactory.getStrategy("invalidType"));
    }

}