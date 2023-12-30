package eus.ibai.jobs.alerts.domain.parse;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static eus.ibai.jobs.alerts.domain.parse.JsAction.*;
import static eus.ibai.jobs.alerts.domain.parse.JsElementType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsStepParserTest {

    @ParameterizedTest
    @MethodSource
    void should_parse_initial_steps(String initialStep, JsStep expectedStep) {
        JsStep parsedStep = JsStepParser.parseStep(initialStep);

        assertThat(parsedStep).isEqualTo(expectedStep);
    }

    @ParameterizedTest
    @MethodSource
    void should_fail_to_parse_invalid_initial_steps(String initialStep) {
        assertThrows(IllegalArgumentException.class, () -> JsStepParser.parseStep(initialStep));
    }

    private static Stream<Arguments> should_parse_initial_steps() {
        return Stream.of(
                Arguments.of("wait:1", new JsStep(WAIT, null, null, "1")),
                Arguments.of("wait:123", new JsStep(WAIT, null, null, "123")),
                Arguments.of("load:id=a", new JsStep(LOAD, ID, "a", null)),
                Arguments.of("load:id=a", new JsStep(LOAD, ID, "a", null)),
                Arguments.of("load:id=Foo-bar_123", new JsStep(LOAD, ID, "Foo-bar_123", null)),
                Arguments.of("load:class=a", new JsStep(LOAD, CLASS, "a", null)),
                Arguments.of("load:class=Foo-bar_123", new JsStep(LOAD, CLASS, "Foo-bar_123", null)),
                Arguments.of("load:title=a", new JsStep(LOAD, TITLE, "a", null)),
                Arguments.of("load:title=Foo Bar 1-2_3", new JsStep(LOAD, TITLE, "Foo Bar 1-2_3", null)),
                Arguments.of("click:id=a", new JsStep(CLICK, ID, "a", null)),
                Arguments.of("click:id=Foo-bar_123", new JsStep(CLICK, ID, "Foo-bar_123", null)),
                Arguments.of("click:class=a", new JsStep(CLICK, CLASS, "a", null)),
                Arguments.of("click:class=a[0]", new JsStep(CLICK, CLASS, "a", "[0]")),
                Arguments.of("click:class=a[0,2,5]", new JsStep(CLICK, CLASS, "a", "[0,2,5]")),
                Arguments.of("click:class=Foo-bar_123", new JsStep(CLICK, CLASS, "Foo-bar_123", null))
        );
    }

    private static Stream<String> should_fail_to_parse_invalid_initial_steps() {
        return Stream.of(
                "wait:",
                "wait:a",
                "wait:1s",
                "load:id=",
                "load:id=foo#bar",
                "load:id=a[0]",
                "load:class=",
                "load:class=foo#bar",
                "load:class=a[0]",
                "load:title=",
                "load:title=foo#bar",
                "load:foo=bar",
                "click:id=",
                "click:id=foo#bar",
                "click:class=",
                "click:class=foo#bar",
                "click:class=a[]",
                "click:class=a[a]",
                "click:class=a[0,2,]",
                "click:title=foo",
                "click:foo=bar",
                "foo:bar"
        );
    }
}