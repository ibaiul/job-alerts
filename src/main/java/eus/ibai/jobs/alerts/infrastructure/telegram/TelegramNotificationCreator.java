package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.notification.NotificationCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static eus.ibai.jobs.alerts.infrastructure.telegram.EmojiUtils.BELL_EMOJI;
import static java.lang.String.format;

@Component
@ConditionalOnProperty(prefix = "telegram", name = "enabled", havingValue = "true")
public class TelegramNotificationCreator implements NotificationCreator<String> {

    private static final String JOB_LINK_TEMPLATE = "- <a href=\"%s\">%s</a>";

    private static final String SITE_UPDATED_TEMPLATE = """
            %s <b> SITE UPDATED </b> %s
            <b><a href="%s">%s</a></b>
            Total Jobs: %s
            
            New Jobs:
            %s
            """;

    private static final String WEEKLY_SUMMARY_TEMPLATE = """
            <b> SITE WEEKLY SUMMARY </b>
            <b><a href="%s">%s</a></b>
            Total Jobs: %s
            """;

    public String createSiteUpdatedNotification(JobSiteSummary jobSiteSummary, List<Job> previousJobs) {
        String titles = jobSiteSummary.jobs().stream()
                .filter(job -> !previousJobs.contains(job))
                .map(job -> format(JOB_LINK_TEMPLATE, job.getUrl(), job.getTitle()))
                .collect(Collectors.joining(System.lineSeparator()));
        return format(SITE_UPDATED_TEMPLATE, BELL_EMOJI, BELL_EMOJI, jobSiteSummary.url(), jobSiteSummary.siteName(), jobSiteSummary.jobs().size(), titles);
    }

    public String createWeeklySummaryNotification(JobSiteSummary jobSiteSummary) {
        return format(WEEKLY_SUMMARY_TEMPLATE, jobSiteSummary.url(), jobSiteSummary.siteName(), jobSiteSummary.jobs().size());
    }
}


