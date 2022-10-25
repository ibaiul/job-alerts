package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifier;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class TelegramAlertNotifier implements AlertNotifier {

    private final List<String> chatIds;
    private final TelegramNotificationCreator notificationFormatter;
    private final TelegramClient client;

    @Override
    public Mono<Void> alertSiteUpdated(JobSiteSummary jobSiteSummary, List<Job> previousJobs) {
        String message = notificationFormatter.createSiteUpdatedNotification(jobSiteSummary, previousJobs);
        return client.send(chatIds, message).then();
    }

    @Override
    public Mono<Void> alertWeeklySummary(JobSiteSummary jobSiteSummary) {
        String message = notificationFormatter.createWeeklySummaryNotification(jobSiteSummary);
        return client.send(chatIds, message).then();
    }
}


