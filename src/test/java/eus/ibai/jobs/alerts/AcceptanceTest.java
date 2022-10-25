package eus.ibai.jobs.alerts;

import com.github.tomakehurst.wiremock.WireMockServer;
import eus.ibai.jobs.alerts.application.JobSiteRegistration;
import eus.ibai.jobs.alerts.infrastructure.email.GreenMailClient;
import eus.ibai.jobs.alerts.infrastructure.email.GreenMailContainer;
import eus.ibai.jobs.alerts.infrastructure.email.TestMimeMessage;
import eus.ibai.jobs.alerts.infrastructure.repository.JobEntityRepository;
import eus.ibai.jobs.alerts.infrastructure.repository.JobSiteEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static eus.ibai.jobs.alerts.TestData.*;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    private static final String TELEGRAM_SEND_MESSAGE_URL = "/bot1234:abcd/sendMessage";

    private static final String TELEGRAM_GET_UPDATES_URL = "/bot1234:abcd/getUpdates";

    private static final int POSTGRES_PORT = 5432;

    private static final WireMockServer wiremock = new WireMockServer(wireMockConfig().dynamicPort());

    private static final PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:13.8")
            .withDatabaseName("acceptance-test-db")
            .withUsername("sa")
            .withPassword("sa")
            .withReuse(true);

    private static final GreenMailContainer<?> smtpContainer = new GreenMailContainer<>("ibaiul/greenmail:latest")
            .withCredentials("job-alert-sender@localhost", "12345678");

    private static final GreenMailClient greenMailClient;

    static {
        postgreSqlContainer.start();
        log.debug("Started PostgreSQL container on port {}", postgreSqlContainer.getMappedPort(POSTGRES_PORT));
        smtpContainer.start();
        log.debug("Started SMTP container on port {} and {}", smtpContainer.getMappedSmtpPort(), smtpContainer.getMappedHttpPort());
        wiremock.start();
        log.debug("Mock HTTP server started on port {}", wiremock.port());
        greenMailClient = new GreenMailClient(smtpContainer.getMappedHttpPort());
    }

    @Autowired
    private JobSiteEntityRepository jobSiteEntityRepository;

    @Autowired
    private JobEntityRepository jobEntityRepository;

    @Autowired
    private JobSiteRegistration jobSiteRegistration;

    @BeforeAll
    static void beforeAll() {
        log.debug("Before all");
        wiremock.start();
    }

    @BeforeEach
    void beforeEach() {
        log.debug("Before each");
        stubJobSite1Ok();
        stubTelegramGetUpdatesSuccessResponse();
        stubTelegramSendMessageSuccessResponse(VALID_CHAT_ID);
    }

    @AfterEach
    void afterEach() {
        log.debug("After each");
        wiremock.resetAll();
        StepVerifier.create(greenMailClient.purgeEmailFromAllMailboxes())
                .verifyComplete();
        StepVerifier.create(jobEntityRepository.deleteAll())
                .verifyComplete();
        StepVerifier.create(jobSiteEntityRepository.deleteAll())
                .verifyComplete();
        jobSiteRegistration.registerJobSites();
    }

    @AfterAll
    static void afterAll() {
        log.debug("After all");
    }

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        log.debug("Setting dynamic properties.");
        registry.add("spring.r2dbc.url", () -> postgreSqlContainer.getJdbcUrl().replaceAll("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.password", postgreSqlContainer::getPassword);
        registry.add("spring.r2dbc.username", postgreSqlContainer::getUsername);
        registry.add("spring.flyway.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.mail.port", smtpContainer::getMappedSmtpPort);
        registry.add("telegram.baseUrl", wiremock::baseUrl);
        registry.add("sites[0].name", () -> JOB_SITE_1_NAME);
        registry.add("sites[0].url", () -> format(JOB_SITE_1_URL_FORMAT, wiremock.baseUrl()));
        registry.add("sites[0].strategy.type", () -> "basicHtml");
        registry.add("sites[0].strategy.steps", () -> "ul.menu_pag,li,a");
        registry.add("sites[0].notifications[0].type", () -> "telegram");
        registry.add("sites[0].notifications[0].recipients[0]", () -> "chatId1");
        registry.add("sites[0].notifications[1].type", () -> "email");
        registry.add("sites[0].notifications[1].recipients[0]", () -> "job-alert-recipient@localhost");
        registry.add("sites[1].name", () -> JOB_SITE_2_NAME);
        registry.add("sites[1].url", () -> format(JOB_SITE_2_URL_FORMAT, wiremock.baseUrl()));
        registry.add("sites[1].strategy.type", () -> "basicHtml");
        registry.add("sites[1].strategy.steps", () -> "a");
    }

    protected static void stubTelegramGetUpdatesSuccessResponse() {
        stubTelegramGetUpdatesResponse(200, TELEGRAM_GET_UPDATES_OK_RESPONSE);
    }

    protected static void stubTelegramGetUpdatesUnavailableResponse() {
        stubTelegramGetUpdatesResponse(503, "{}");
    }

    private static void stubTelegramGetUpdatesResponse(int statusCode, String response) {
        wiremock.stubFor(get(urlMatching(TELEGRAM_GET_UPDATES_URL))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    protected static void stubTelegramSendMessageSuccessResponse(String chatId) {
        stubTelegramSendMessageResponse(chatId, ".*", 200, TELEGRAM_SEND_MESSAGE_OK_RESPONSE);
    }

    protected static void stubTelegramSendMessageSuccessResponse(String chatId, String message) {
        stubTelegramSendMessageResponse(chatId, message, 200, TELEGRAM_SEND_MESSAGE_OK_RESPONSE);
    }

    protected static void stubTelegramSendMessageBadRequestResponse(String chatId, String message) {
        stubTelegramSendMessageResponse(chatId, message, 400, TELEGRAM_SEND_MESSAGE_BAD_REQUEST_RESPONSE);
    }

    private static void stubTelegramSendMessageResponse(String chatId, String message, int statusCode, String response) {
        wiremock.stubFor(post(urlEqualTo(TELEGRAM_SEND_MESSAGE_URL))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(matchingJsonPath("$.chat_id", equalTo(chatId)))
                .withRequestBody(matchingJsonPath("$.parse_mode", equalTo("html")))
                .withRequestBody(matchingJsonPath("$.disable_web_page_preview", equalTo("true")))
                .withRequestBody(matchingJsonPath("$.disable_notification", equalTo("true")))
                .withRequestBody(matchingJsonPath("$.text", matching(message)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    protected void stubJobSite1Ok() {
        stubJobSite1(200, jobSite1Response(wiremockBaseUrl()));
    }

    private static void stubJobSite1(int statusCode, String response) {
        wiremock.stubFor(get(urlMatching("/job-site-1"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "text/html")
                        .withBody(response)));
    }

    protected void verifyTelegramMessageSent(String chatId, int count) {
        wiremock.verify(exactly(count), postRequestedFor(urlEqualTo(TELEGRAM_SEND_MESSAGE_URL))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(matchingJsonPath("$.chat_id", equalTo(chatId)))
                .withRequestBody(matchingJsonPath("$.parse_mode", equalTo("html")))
                .withRequestBody(matchingJsonPath("$.disable_web_page_preview", equalTo("true")))
                .withRequestBody(matchingJsonPath("$.disable_notification", equalTo("true")))
                .withRequestBody(matchingJsonPath("$.text", matching(".*"))));
    }

    protected void verifyTelegramMessageSent(String chatId, String message) {
        wiremock.verify(exactly(1), postRequestedFor(urlEqualTo(TELEGRAM_SEND_MESSAGE_URL))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(matchingJsonPath("$.chat_id", equalTo(chatId)))
                .withRequestBody(matchingJsonPath("$.parse_mode", equalTo("html")))
                .withRequestBody(matchingJsonPath("$.disable_web_page_preview", equalTo("true")))
                .withRequestBody(matchingJsonPath("$.disable_notification", equalTo("true")))
                .withRequestBody(matchingJsonPath("$.text", equalTo(message))));
    }

    protected void verifyNoTelegramMessageSent() {
        wiremock.verify(exactly(0), postRequestedFor(urlEqualTo(TELEGRAM_SEND_MESSAGE_URL)));
    }

    protected void verifyEmailSent(String recipient, String subject) {
        StepVerifier.create(greenMailClient.getAllEmails())
                .expectNext(new TestMimeMessage(List.of(recipient), subject))
                .verifyComplete();
    }

    protected void verifyEmailSent(String recipient1, String recipient2, String subject) {
        StepVerifier.create(greenMailClient.getAllEmails())
                .expectNext(new TestMimeMessage(List.of(recipient1), subject))
                .expectNext(new TestMimeMessage(List.of(recipient2), subject))
                .verifyComplete();
    }

    protected void verifyNoEmailSent() {
        StepVerifier.create(greenMailClient.getAllEmails())
                .verifyComplete();
    }

    protected String wiremockBaseUrl() {
        return wiremock.baseUrl();
    }
}