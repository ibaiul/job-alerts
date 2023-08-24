package eus.ibai.jobs.alerts.infrastructure.email;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GreenMailClient {

    private final WebClient webClient;

    public GreenMailClient(int httpPort) {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + httpPort)
                .build();
    }

    public Mono<Void> purgeEmailFromAllMailboxes() {
        return webClient.post().uri("/api/mail/purge")
                .retrieve()
                .bodyToMono(String.class)
                .filter(response -> response.contains("Purged mails"))
                .switchIfEmpty(Mono.error(new IllegalStateException()))
                .then();
    }

    public Mono<Void> reset() {
        return webClient.post().uri("/api/service/reset")
                .retrieve()
                .bodyToMono(String.class)
                .filter(response -> response.contains("Performed reset"))
                .switchIfEmpty(Mono.error(new IllegalStateException()))
                .then();
    }

    public Flux<TestMimeMessage> getAllEmails() {
        return webClient.get().uri("/api/mail")
                .retrieve()
                .bodyToFlux(TestMimeMessage.class);
    }
}
