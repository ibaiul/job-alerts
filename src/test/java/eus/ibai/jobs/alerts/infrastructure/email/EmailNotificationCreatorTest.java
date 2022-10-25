package eus.ibai.jobs.alerts.infrastructure.email;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailNotificationCreatorTest {

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private EmailNotificationCreator emailNotificationCreator;

    @Test
    void should_include_changed_jobs_only_when_site_updated_message_created() {
        String sender = "from";
        when(emailProperties.getFrom()).thenReturn(sender);
        Job job1 = new Job("jobTitle1", "jobUrl1");
        List<Job> previousJobs = List.of(job1);
        Job job2 = new Job("jobTitle2", "jobUrl2");
        List<Job> currentJobs = List.of(job1, job2);
        String siteName = "siteName";
        String siteUrl = "siteUrl";
        JobSiteSummary summary = new JobSiteSummary(siteName, siteUrl, currentJobs);

        EmailNotification siteUpdatedNotification = emailNotificationCreator.createSiteUpdatedNotification(summary, previousJobs);

        assertThat(siteUpdatedNotification.from(), equalTo(sender));
        assertThat(siteUpdatedNotification.subject(), containsString(siteName));
        assertThat(siteUpdatedNotification.body(), containsString(siteName));
        assertThat(siteUpdatedNotification.body(), not(containsString(job1.getTitle())));
        assertThat(siteUpdatedNotification.body(), not(containsString(job1.getUrl())));
        assertThat(siteUpdatedNotification.body(), containsString(job2.getTitle()));
        assertThat(siteUpdatedNotification.body(), containsString(job2.getUrl()));
    }
}