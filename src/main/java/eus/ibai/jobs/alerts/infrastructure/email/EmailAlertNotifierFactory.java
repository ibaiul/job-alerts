package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.domain.notification.AlertNotifier;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifierFactory;
import eus.ibai.jobs.alerts.domain.notification.NotifierType;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import static eus.ibai.jobs.alerts.domain.notification.NotifierType.EMAIL;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
public class EmailAlertNotifierFactory implements AlertNotifierFactory {

    private final EmailNotificationCreator emailNotificationCreator;

    private final EmailClient emailClient;

    @Override
    public NotifierType getNotifierType() {
        return EMAIL;
    }

    @Override
    public AlertNotifier create(List<String> recipients) {
        return new EmailAlertNotifier(recipients, emailNotificationCreator, emailClient);
    }
}
