package eus.ibai.jobs.alerts.infrastructure.telegram;

import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTags;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.regex.Pattern;

public class TelegramWebClientExchangeTagsProvider implements WebClientExchangeTagsProvider {

    private static final Pattern PATTERN_BEFORE_PATH = Pattern.compile("^https?://[^/]+/");
    private static final Pattern PATTERN_BOT_TOKEN = Pattern.compile("/bot.+/");

    private static final String URI_TEMPLATE_ATTRIBUTE = WebClient.class.getName() + ".uriTemplate";

    @Override
    public Iterable<Tag> tags(ClientRequest request, ClientResponse response, Throwable throwable) {
        Tag method = WebClientExchangeTags.method(request);
        Tag uri = uri(request);
        Tag clientName = WebClientExchangeTags.clientName(request);
        Tag status = WebClientExchangeTags.status(response, throwable);
        Tag outcome = WebClientExchangeTags.outcome(response);
        return Arrays.asList(method, uri, clientName, status, outcome);
    }

    private Tag uri(ClientRequest request) {
        String uri = (String) request.attribute(URI_TEMPLATE_ATTRIBUTE).orElseGet(() -> request.url().toString());
        return Tag.of("uri", extractPathWithoutBotToken(uri));
    }

    private String extractPathWithoutBotToken(String url) {
        String path = PATTERN_BEFORE_PATH.matcher(url).replaceFirst("");
        String curatedPath = path.startsWith("/") ? path : '/' + path;
        return PATTERN_BOT_TOKEN.matcher(curatedPath).replaceFirst("/");
    }
}
