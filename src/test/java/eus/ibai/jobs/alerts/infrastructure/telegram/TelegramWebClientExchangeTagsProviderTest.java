package eus.ibai.jobs.alerts.infrastructure.telegram;

import io.micrometer.core.instrument.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;

class TelegramWebClientExchangeTagsProviderTest {

    private TelegramWebClientExchangeTagsProvider tagsProvider;

    @BeforeEach
    void beforeEach() {
        tagsProvider = new TelegramWebClientExchangeTagsProvider();
    }

    @Test
    void should_remove_sensitive_bot_token_from_uri_tag() throws URISyntaxException {
        String sensitiveUri = "/botSensitive:Token/getUpdates";
        String expectedUri = "/getUpdates";
        ClientRequest clientRequest = ClientRequest.create(HttpMethod.GET, new URI(sensitiveUri)).build();
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK).build();

        Iterable<Tag> tags = tagsProvider.tags(clientRequest, clientResponse, null);

        assertThat(tags, hasItem(Tag.of("uri", expectedUri)));
    }

    @Test
    void should_provide_default_web_client_tags() throws URISyntaxException {
        ClientRequest clientRequest = ClientRequest.create(HttpMethod.GET, new URI("http://localhost/path")).build();
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK).build();

        Iterable<Tag> tags = tagsProvider.tags(clientRequest, clientResponse, null);

        assertThat(tags, containsInAnyOrder(
                Tag.of("method", "GET"),
                Tag.of("uri", "/path"),
                Tag.of("client.name", "localhost"),
                Tag.of("status", "200"),
                Tag.of("outcome", "SUCCESS")
        ));
    }
}