package eus.ibai.jobs.alerts.domain.notification;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AlertNotifierProvider {

    private final Map<NotifierType, AlertNotifierFactory> alertNotifierFactoryMap = new EnumMap<>(NotifierType.class);

    public AlertNotifierProvider(List<AlertNotifierFactory> alertNotifierFactories) {
        alertNotifierFactories.forEach(alertNotifierFactory -> alertNotifierFactoryMap.put(alertNotifierFactory.getNotifierType(), alertNotifierFactory));
    }

    public List<AlertNotifier> createNotifiers(List<NotificationTargets> alertNotifierSettings) {
        if (alertNotifierSettings == null || alertNotifierSettings.isEmpty()) {
            return Collections.emptyList();
        }

        return alertNotifierSettings.stream()
                .map(this::toAlertNotifier)
                .toList();
    }

    private AlertNotifier toAlertNotifier(NotificationTargets notificationTargets) {
        return Optional.ofNullable(alertNotifierFactoryMap.get(notificationTargets.getType()))
                .map(alertNotifierFactory -> alertNotifierFactory.create(notificationTargets.getRecipients()))
                .orElseThrow(() -> new IllegalArgumentException("Could not find AlertNotifierFactory for type " + notificationTargets.getType()));
    }
}
