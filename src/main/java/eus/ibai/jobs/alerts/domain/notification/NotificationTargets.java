package eus.ibai.jobs.alerts.domain.notification;

import lombok.ToString;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class NotificationTargets {

    NotifierType type;

    List<String> recipients;

    @ToString.Include(name = "recipients")
    private String sensitiveRecipientMasker() {
        return recipients.stream()
                .map(recipient -> "*".repeat(recipient.length()))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}