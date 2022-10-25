package eus.ibai.jobs.alerts.domain.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static eus.ibai.jobs.alerts.domain.notification.NotifierType.EMAIL;
import static eus.ibai.jobs.alerts.domain.notification.NotifierType.TELEGRAM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertNotifierProviderTest {

    private final List<String> recipients = List.of("recipient1");

    @Mock
    private AlertNotifierFactory alertNotifierFactory;

    @Mock
    private AlertNotifier alertNotifier;

    private AlertNotifierProvider alertNotifierProvider;

    @BeforeEach
    void beforeEach() {
        when(alertNotifierFactory.getNotifierType()).thenReturn(TELEGRAM);
        alertNotifierProvider = new AlertNotifierProvider(List.of(alertNotifierFactory));
    }

    @Test
    void should_create_alert_notifiers_when_defined_in_notifier_settings() {
        when(alertNotifierFactory.create(recipients)).thenReturn(alertNotifier);
        List<NotificationTargets> notificationTargets = List.of(new NotificationTargets(TELEGRAM, recipients));

        List<AlertNotifier> alertNotifiers = alertNotifierProvider.createNotifiers(notificationTargets);

        assertThat(alertNotifiers, equalTo(List.of(alertNotifier)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void should_create_empty_alert_notifiers_when_not_defined_in_notifier_settings(List<NotificationTargets> notificationTargets) {
        List<AlertNotifier> alertNotifiers = alertNotifierProvider.createNotifiers(notificationTargets);

        assertThat(alertNotifiers, hasSize(0));
    }

    @Test
    void should_fail_creating_alert_notifiers_when_requested_notifier_type_not_registered() {
        NotificationTargets notificationTargets = new NotificationTargets(EMAIL, recipients);
        List<NotificationTargets> notificationTargetList = List.of(notificationTargets);

        assertThrows(IllegalArgumentException.class, () -> alertNotifierProvider.createNotifiers(notificationTargetList));
    }
}