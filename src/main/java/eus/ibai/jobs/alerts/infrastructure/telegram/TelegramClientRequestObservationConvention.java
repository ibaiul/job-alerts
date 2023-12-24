package eus.ibai.jobs.alerts.infrastructure.telegram;

import io.micrometer.common.KeyValue;
import org.springframework.web.reactive.function.client.ClientHttpObservationDocumentation.HighCardinalityKeyNames;
import org.springframework.web.reactive.function.client.ClientHttpObservationDocumentation.LowCardinalityKeyNames;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientRequestObservationContext;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;

import java.util.regex.Pattern;

public class TelegramClientRequestObservationConvention extends DefaultClientRequestObservationConvention {

    private static final Pattern PATTERN_BEFORE_PATH = Pattern.compile("^https?://[^/]+/");
    private static final Pattern PATTERN_BOT_TOKEN = Pattern.compile("/bot.+/");

    public TelegramClientRequestObservationConvention(String name) {
        super(name);
    }

    @Override
    protected KeyValue uri(ClientRequestObservationContext context) {
        if (context.getUriTemplate() != null) {
            return KeyValue.of(LowCardinalityKeyNames.URI, extractPathWithoutBotToken(context.getUriTemplate()));
        }
        return KeyValue.of(LowCardinalityKeyNames.URI, KeyValue.NONE_VALUE);
    }

    @Override
    protected KeyValue httpUrl(ClientRequestObservationContext context) {
        ClientRequest request = context.getRequest();
        if (request != null) {
            return KeyValue.of(HighCardinalityKeyNames.HTTP_URL, extractPathWithoutBotToken(request.url().toASCIIString()));
        }
        return KeyValue.of(HighCardinalityKeyNames.HTTP_URL, KeyValue.NONE_VALUE);
    }

    private String extractPathWithoutBotToken(String url) {
        String path = PATTERN_BEFORE_PATH.matcher(url).replaceFirst("");
        String curatedPath = path.startsWith("/") ? path : '/' + path;
        return PATTERN_BOT_TOKEN.matcher(curatedPath).replaceFirst("/");
    }
}
