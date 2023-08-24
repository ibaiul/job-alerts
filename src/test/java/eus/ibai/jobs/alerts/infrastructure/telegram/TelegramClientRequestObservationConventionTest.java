package eus.ibai.jobs.alerts.infrastructure.telegram;

import io.micrometer.common.KeyValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientRequestObservationContext;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TelegramClientRequestObservationConventionTest {

    private TelegramClientRequestObservationConvention observationConvention;

    @BeforeEach
    void beforeEach() {
        observationConvention = new TelegramClientRequestObservationConvention("http.out.telegram");
    }

    @Test
    void should_remove_sensitive_bot_token_from_uri_tag() throws URISyntaxException {
        String sensitiveUri = "https://localhost/botSensitive:Token/getUpdates";
        String expectedUri = "/getUpdates";
        ClientRequest clientRequest = ClientRequest.create(HttpMethod.GET, new URI(sensitiveUri)).build();
        ClientRequestObservationContext observationContext = mock(ClientRequestObservationContext.class);
        when(observationContext.getUriTemplate()).thenReturn(sensitiveUri);
        when(observationContext.getRequest()).thenReturn(clientRequest);

        List<KeyValue> highCardinalityKeyValues = observationConvention.getHighCardinalityKeyValues(observationContext).stream().toList();
        List<KeyValue> lowCardinalityKeyValues = observationConvention.getLowCardinalityKeyValues(observationContext).stream().toList();

        assertThat(highCardinalityKeyValues, hasItem(KeyValue.of("http.url", expectedUri)));
        assertThat(lowCardinalityKeyValues, hasItem(KeyValue.of("uri", expectedUri)));
    }

    @Test
    void should_provide_default_web_client_tags() throws URISyntaxException {
        String expectedUri = "http://localhost/path";
        ClientRequest clientRequest = ClientRequest.create(HttpMethod.GET, new URI(expectedUri)).build();
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK).build();
        ClientRequestObservationContext observationContext = mock(ClientRequestObservationContext.class);
        when(observationContext.getUriTemplate()).thenReturn(expectedUri);
        when(observationContext.getRequest()).thenReturn(clientRequest);
        when(observationContext.getResponse()).thenReturn(clientResponse);

        List<KeyValue> highCardinalityKeyValues = observationConvention.getHighCardinalityKeyValues(observationContext).stream().toList();
        List<KeyValue> lowCardinalityKeyValues = observationConvention.getLowCardinalityKeyValues(observationContext).stream().toList();

        assertThat(highCardinalityKeyValues, hasItem(KeyValue.of("http.url", "/path")));
        assertThat(lowCardinalityKeyValues, containsInAnyOrder(
                KeyValue.of("uri", "/path"),
                KeyValue.of("method", "GET"),
                KeyValue.of("client.name", "localhost"),
                KeyValue.of("status", "200"),
                KeyValue.of("outcome", "SUCCESS"),
                KeyValue.of("exception", "none")));
    }
}