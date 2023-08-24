package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class InfoEndpointTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void should_return_application_build_and_git_info() {
        await().atMost(3, SECONDS).untilAsserted(
                () -> webTestClient.get().uri("/actuator/info")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.app.name").isEqualTo("Job Alerts")
                        .jsonPath("$.app.description").isNotEmpty()
                        .jsonPath("$.app.version").isNotEmpty()
                        .jsonPath("$.git.branch").isNotEmpty()
                        .jsonPath("$.git.commit.id").isNotEmpty()
                        .jsonPath("$.git.commit.time").isNotEmpty()
                        .jsonPath("$.build.group").isEqualTo("eus.ibai")
                        .jsonPath("$.build.artifact").isEqualTo("job-alerts")
                        .jsonPath("$.build.name").isEqualTo("job-alerts")
                        .jsonPath("$.build.time").isNotEmpty()
                        .jsonPath("$.build.version").isNotEmpty());
                ;
    }
}
