package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.notification.NotificationCreator;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

// Integrate thymeleaf or other HTML template engine

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "true")
public class EmailNotificationCreator implements NotificationCreator<EmailNotification> {

    private static final String JOB_LINK_TEMPLATE = "- <a href=\"%s\">%s</a>";

    private static final String SITE_UPDATED_SUBJECT_TEMPLATE = "Job Site Updated: \"%s\"";

    private static final String SITE_UPDATED_TEMPLATE = """
            <html><body>
            <a href="%s"><h3>%s</h3></a>
            Total Jobs: %s</br></br>
            New jobs:</br>
            %s
            </body></html>
            """;

    private static final String WEEKLY_SUMMARY_SUBJECT_TEMPLATE = "Job Site Weekly Summary: \"%s\"";

    private static final String WEEKLY_SUMMARY_TEMPLATE = """
            <html><body>"
            <a href="%s"><h3>%s</h3></a>
            Total Jobs: %s
            </body></html>
            """;

    private final EmailProperties emailProperties;

    public EmailNotification createSiteUpdatedNotification(JobSiteSummary summary, List<Job> previousJobs) {
        String htmlJobs = summary.jobs().stream()
                .filter(job -> !previousJobs.contains(job))
                .map(job -> format(JOB_LINK_TEMPLATE, job.getUrl(), job.getTitle()))
                .collect(Collectors.joining("</br>"));
        String body = format(SITE_UPDATED_TEMPLATE, summary.url(), summary.siteName(), summary.jobs().size(), htmlJobs);
        return new EmailNotification(emailProperties.getFrom(), format(SITE_UPDATED_SUBJECT_TEMPLATE, summary.siteName()), body);
    }

    public EmailNotification createWeeklySummaryNotification(JobSiteSummary summary) {
        String body = format(WEEKLY_SUMMARY_TEMPLATE, summary.url(), summary.siteName(), summary.jobs().size());
        return new EmailNotification(emailProperties.getFrom(), format(WEEKLY_SUMMARY_SUBJECT_TEMPLATE, summary.siteName()), body);
    }
}


