package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifier;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class EmailAlertNotifier implements AlertNotifier {

    private final List<String> emailAddresses;

    private final EmailNotificationCreator notificationFormatter;

    private final EmailClient client;

    @Override
    public Mono<Void> alertSiteUpdated(JobSiteSummary jobSiteSummary, List<Job> previousJobs) {
        EmailNotification message = notificationFormatter.createSiteUpdatedNotification(jobSiteSummary, previousJobs);
        return client.send(emailAddresses, message)
                .then();
    }

    @Override
    public Mono<Void> alertWeeklySummary(JobSiteSummary jobSiteSummary) {
        EmailNotification message = notificationFormatter.createWeeklySummaryNotification(jobSiteSummary);
        return client.send(emailAddresses, message)
                .then();
    }
}


