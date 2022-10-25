package eus.ibai.jobs.alerts.infrastructure.telegram;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

class TelegramNotificationCreatorTest {

    private TelegramNotificationCreator telegramNotificationCreator = new TelegramNotificationCreator();

    @Test
    void should_include_changed_jobs_only_when_site_updated_message_created() {
        Job job1 = new Job("jobTitle1", "jobUrl1");
        List<Job> previousJobs = List.of(job1);
        Job job2 = new Job("jobTitle2", "jobUrl2");
        List<Job> currentJobs = List.of(job1, job2);
        String siteName = "siteName";
        String siteUrl = "siteUrl";
        JobSiteSummary summary = new JobSiteSummary(siteName, siteUrl, currentJobs);

        String siteUpdatedNotification = telegramNotificationCreator.createSiteUpdatedNotification(summary, previousJobs);

        assertThat(siteUpdatedNotification, containsString(siteName));
        assertThat(siteUpdatedNotification, containsString(siteUrl));
        assertThat(siteUpdatedNotification, not(containsString(job1.getTitle())));
        assertThat(siteUpdatedNotification, not(containsString(job1.getUrl())));
        assertThat(siteUpdatedNotification, containsString(job2.getTitle()));
        assertThat(siteUpdatedNotification, containsString(job2.getUrl()));
    }
}