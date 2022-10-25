package eus.ibai.jobs.alerts.domain.notification;

import org.junit.jupiter.api.Test;

import java.util.List;

import static eus.ibai.jobs.alerts.domain.notification.NotifierType.EMAIL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

class NotificationTargetsTest {

    @Test
    void should_mask_recipients_when_printing_to_string() {
        String recipient = "test@example.com";
        NotificationTargets notificationTargets = new NotificationTargets(EMAIL, List.of(recipient));
        String maskedRecipient = "****************";

        String notificationTargetsString = notificationTargets.toString();

        assertThat(notificationTargetsString, not(containsString(recipient)));
        assertThat(notificationTargetsString, containsString(maskedRecipient));
    }
}