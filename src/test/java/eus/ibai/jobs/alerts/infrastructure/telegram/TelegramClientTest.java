package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.AcceptanceTest;
import eus.ibai.jobs.alerts.domain.notification.NotificationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

import static eus.ibai.jobs.alerts.TestData.VALID_CHAT_ID;

class TelegramClientTest extends AcceptanceTest {

    @Autowired
    private TelegramClient telegramClient;

    @Test
    void should_send_message_successfully() {
        String expectedMessage = "message1";
        stubTelegramSendMessageSuccessResponse(VALID_CHAT_ID, expectedMessage);

        StepVerifier.create(telegramClient.send(List.of(VALID_CHAT_ID), expectedMessage))
                .expectNextCount(1)
                .verifyComplete();

        verifyTelegramMessageSent(VALID_CHAT_ID, expectedMessage);
        verifyTelegramMessageSentMetricRecorded(200, 1L);
    }

    @Test
    void should_throw_exception_when_message_not_sent() {
        String expectedMessage = "message1";
        stubTelegramSendMessageBadRequestResponse(VALID_CHAT_ID, expectedMessage);

        StepVerifier.create(telegramClient.send(List.of(VALID_CHAT_ID), expectedMessage))
                .expectNextCount(0)
                .verifyError(NotificationException.class);

        verifyTelegramMessageSent(VALID_CHAT_ID, expectedMessage);
        verifyTelegramMessageSentMetricRecorded(400, 1L);
    }
}