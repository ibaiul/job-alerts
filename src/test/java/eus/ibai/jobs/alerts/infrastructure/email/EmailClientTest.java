package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.AcceptanceTest;
import eus.ibai.jobs.alerts.domain.notification.NotificationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

class EmailClientTest extends AcceptanceTest {

    @Autowired
    private EmailClient emailClient;

    @Test
    void should_send_email() {
        String from = "test0@localhost";
        String subject = "Subject1";
        String htmlBody = "<html><body>Foo</body></html>";
        String recipient1 = "test1@localhost";
        String recipient2 = "test2@localhost";

        StepVerifier.create(emailClient.send(List.of(recipient1, recipient2), new EmailNotification(from, subject, htmlBody)))
                .expectNextCount(0)
                .verifyComplete();

        verifyEmailSent(recipient1, recipient2, subject);
    }

    @Test
    void should_throw_exception_when_invalid_recipient() {
        String from = "test0@localhost";
        String subject = "Subject1";
        String htmlBody = "<html><body>Foo</body></html>";
        String recipient1 = "test1@@@localhost";

        StepVerifier.create(emailClient.send(List.of(recipient1), new EmailNotification(from, subject, htmlBody)))
                .expectNextCount(0)
                .verifyErrorMatches(throwable -> throwable instanceof NotificationException);
    }
}