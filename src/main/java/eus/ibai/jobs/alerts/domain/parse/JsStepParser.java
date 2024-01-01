package eus.ibai.jobs.alerts.domain.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsStepParser {

    private static final Pattern STEP_WAIT_PATTERN = Pattern.compile("wait:(\\d+)");

    private static final Pattern STEP_LOAD_PATTERN = Pattern.compile("load:(id|class|title)=([a-zA-Z0-9_\\- ]+)");

    private static final Pattern STEP_CLICK_PATTERN = Pattern.compile("click:(id|class)=([a-zA-Z0-9_\\-]+)(\\[(\\d+(,\\d+)*+)\\])?");

    public JsStep parseStep(String initialStep) {
        JsAction jsAction = JsAction.valueOf(initialStep.split(":")[0].toUpperCase());
        return switch (jsAction) {
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
    }
}
