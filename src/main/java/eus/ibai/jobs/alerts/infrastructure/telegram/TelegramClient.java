package eus.ibai.jobs.alerts.infrastructure.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import eus.ibai.jobs.alerts.domain.notification.NotificationClient;
import eus.ibai.jobs.alerts.domain.notification.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramClient implements NotificationClient<String, String> {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36";

    private static final String HEALTHCHECK_URL_FORMAT = "/bot%s/getUpdates";

    private static final String SEND_MESSAGE_URL_FORMAT = "/bot%s/sendMessage";

    private final TelegramProperties properties;

    private final WebClient webClient;

    public TelegramClient(TelegramProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .followRedirect(true)))
                .build();
    }

    @Override
    public Flux<String> send(List<String> chatIds, String message) {
        return Flux.fromIterable(chatIds)
                .flatMap(chatId -> sendMessage(chatId, message))
                .doOnError(error -> log.error("Failed to send Telegram message. Error: {}", error.getMessage()))
                .onErrorMap(error -> error instanceof NotificationException ? error : new NotificationException(error));
    }

    public Mono<String> checkHealth() {
        return webClient.get()
                .uri(format(HEALTHCHECK_URL_FORMAT, properties.getBotKey()))
                .exchangeToMono(this::parseResponse)
                .doOnError(error -> log.error("Failed to check Telegram health. Error: {}", error.getMessage()));
    }

    private Mono<String> sendMessage(String chatId, String message) {
        SendMessageRequest requestBody = new SendMessageRequest(chatId, "html", true, true, message);
        return webClient.post()
                .uri(format(SEND_MESSAGE_URL_FORMAT, properties.getBotKey()))
                .body(Mono.just(requestBody), SendMessageRequest.class)
                .exchangeToMono(this::parseResponse)
                .doOnSuccess(response -> log.info("Sent message to Telegram chat {}.", chatId));
    }

    private Mono<String> parseResponse(ClientResponse httpResponse) {
        return httpResponse.bodyToMono(String.class)
                .handle((body, sink) -> {
                    log.debug("Received response from Telegram: {} -> {}", httpResponse.statusCode(), body);
                    if (httpResponse.statusCode().isError()) {
                        sink.error(new NotificationException("Telegram request failed. Status: " + httpResponse.rawStatusCode() + ",  Response: " + body));
                    } else {
                        sink.next(body);
                    }
                });
    }

    public record SendMessageRequest(@JsonProperty("chat_id") String chatId,
                                     @JsonProperty("parse_mode") String parseMode,
                                     @JsonProperty("disable_web_page_preview") boolean disableWebPagePreview,
                                     @JsonProperty("disable_notification") boolean disableNotification,
                                     @JsonProperty("text") String message) {
    }
}
