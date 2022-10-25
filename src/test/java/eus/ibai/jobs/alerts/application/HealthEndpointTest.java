package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

class HealthEndpointTest extends AcceptanceTest {

    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void should_return_ok_when_application_healthy() {
        await().atMost(3, SECONDS).untilAsserted(
                () -> webTestClient.get().uri("/actuator/health")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.status").isEqualTo(UP)
                        .jsonPath("$.components.r2dbc.status").isEqualTo(UP)
                        .jsonPath("$.components.mail.status").isEqualTo(UP)
                        .jsonPath("$.components.telegram.status").isEqualTo(UP));

    }

    @Test
    void should_return_service_unavailable_when_application_unhealthy() {
        stubTelegramGetUpdatesUnavailableResponse();

        await().atMost(3, SECONDS).untilAsserted(
                () -> webTestClient.get().uri("/actuator/health")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isEqualTo(SERVICE_UNAVAILABLE)
                        .expectBody()
                        .jsonPath("$.status").isEqualTo(DOWN)
                        .jsonPath("$.components.r2dbc.status").isEqualTo(UP)
                        .jsonPath("$.components.mail.status").isEqualTo(UP)
                        .jsonPath("$.components.telegram.status").isEqualTo(DOWN));
    }
}
