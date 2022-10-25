package eus.ibai.jobs.alerts.infrastructure.jsoup;

import eus.ibai.jobs.alerts.domain.parse.ParsingException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class JsoupClient {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36";

    private static final int BUFFER_SIZE = 5 * 1024 * 1024;

    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                    .followRedirect(true)
                    .compress(true)))
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
                    .build())
            .build();

    public Mono<Document> parse(String siteUrl) {
        log.trace("Retrieving DOM from {}", siteUrl);
        return webClient.get()
                .uri(siteUrl)
                .exchangeToMono(clientResponse -> parseResponse(clientResponse, siteUrl))
                .onErrorMap(error -> error instanceof ParsingException ? error : new ParsingException(error))
                .switchIfEmpty(Mono.error(new ParsingException("Could not retrieve jobs from site " + siteUrl)))
                .map(Jsoup::parse);
    }

    private Mono<String> parseResponse(ClientResponse httpResponse, String siteUrl) {
        return httpResponse.bodyToMono(String.class)
                .handle((body, sink) -> {
                    if (httpResponse.statusCode().isError()) {
                        sink.error(new ParsingException("Jsoup request failed for site " + siteUrl + ". Status: " + httpResponse.rawStatusCode() + ",  Response: " + body));
                    } else {
                        sink.next(body);
                    }
                });
    }
}
