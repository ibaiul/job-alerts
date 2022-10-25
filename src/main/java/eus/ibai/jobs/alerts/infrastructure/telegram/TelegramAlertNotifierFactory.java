package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.domain.notification.AlertNotifier;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifierFactory;
import eus.ibai.jobs.alerts.domain.notification.NotifierType;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import static eus.ibai.jobs.alerts.domain.notification.NotifierType.TELEGRAM;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramAlertNotifierFactory implements AlertNotifierFactory {

    private final TelegramNotificationCreator telegramNotificationCreator;

    private final TelegramClient telegramClient;

    @Override
    public NotifierType getNotifierType() {
        return TELEGRAM;
    }

    @Override
    public AlertNotifier create(List<String> recipients) {
        return new TelegramAlertNotifier(recipients, telegramNotificationCreator, telegramClient);
    }
}
