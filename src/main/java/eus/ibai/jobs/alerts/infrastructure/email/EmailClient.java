package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.domain.notification.NotificationClient;
import eus.ibai.jobs.alerts.domain.notification.NotificationException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
public class EmailClient implements NotificationClient<EmailNotification, Void> {

    private final JavaMailSender emailSender;

    @Override
    public Flux<Void> send(List<String> emailAddresses, EmailNotification emailNotification) {
        return Flux.fromIterable(emailAddresses)
                .flatMap(emailAddress -> sendEmail(emailNotification.from(), emailAddress, emailNotification.subject(), emailNotification.body()))
                .onErrorMap(error -> {
                    log.error("Failed to send email message.", error);
                    return new NotificationException(error);
                });
    }

    public Mono<Void> checkHealth() {
        return Mono.fromRunnable(this::testConnection);
    }

    private Mono<Void> sendEmail(String from, String to, String subject, String body) {
        return Mono.fromCallable(() -> prepareEmail(from, to, subject, body))
                .doOnSuccess(mimeMessage -> log.debug("Sending Email with subject {}", subject))
                .doOnNext(emailSender::send)
                .doOnSuccess(mimeMessage -> log.info("Sent email with subject {}", subject))
                .then();
    }

    @SneakyThrows
    private MimeMessage prepareEmail(String from, String to, String subject, String body) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        return mimeMessage;
    }

    @SneakyThrows
    private void testConnection() {
        ((JavaMailSenderImpl) emailSender).testConnection();
    }
}
