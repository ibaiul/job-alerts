package eus.ibai.jobs.alerts.domain.parse;

public record JsStep(JsAction action, JsElementType elementType, String elementValue, String extra) {

    public JsStep(JsAction action, JsElementType elementType, String elementValue) {
        this(action, elementType, elementValue, null);
    }
}
