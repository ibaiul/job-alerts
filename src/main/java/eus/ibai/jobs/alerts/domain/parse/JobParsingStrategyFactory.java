package eus.ibai.jobs.alerts.domain.parse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JobParsingStrategyFactory {

    private final List<JobParsingStrategy> parsingStrategies;

    public JobParsingStrategy getStrategy(String type) {
        return parsingStrategies.stream()
                .filter(parsingStrategy -> parsingStrategy.getType().equals(type))
                .findFirst()
                .orElseThrow(() ->new IllegalArgumentException("Unable to find parsing strategy " + type));
    }
}
