package eus.ibai.jobs.alerts.domain.alert;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSite;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;
import eus.ibai.jobs.alerts.domain.notification.AlertNotifier;
import eus.ibai.jobs.alerts.domain.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSiteAlerterTest {

    @Mock
    private JobSite jobSite;

    @Mock
    private AlertNotifier alertNotifier1;

    @Mock
    private AlertNotifier alertNotifier2;

    @Mock
    private JobRepository jobRepository;

    private JobSiteAlerter jobSiteAlerter;

    private final Job job = new Job("job1", "jobUrl1");

    private final JobSiteSummary summary = new JobSiteSummary("siteName1", "siteUrl1", List.of(job));

    @BeforeEach
    void beforeEach() {
        jobSiteAlerter = new JobSiteAlerter(jobSite, List.of(alertNotifier1, alertNotifier2), jobRepository);
        when(jobSite.getSummary()).thenReturn(Mono.just(summary));
    }

    @Test
    void should_notify_when_site_jobs_not_in_sync() {
        when(jobSite.getName()).thenReturn(summary.siteName());
        when(jobRepository.getEnabledJobsBySiteName(summary.siteName())).thenReturn(Flux.empty());
        when(alertNotifier1.alertSiteUpdated(summary, emptyList())).thenReturn(Mono.empty());
        when(alertNotifier2.alertSiteUpdated(summary, emptyList())).thenReturn(Mono.empty());


        StepVerifier.create(jobSiteAlerter.checkSite())
                .expectNext(summary)
                .verifyComplete();

        verify(alertNotifier1, times(1)).alertSiteUpdated(summary, emptyList());
        verify(alertNotifier2, times(1)).alertSiteUpdated(summary, emptyList());
    }

    @Test
    void should_not_alert_when_site_jobs_are_in_sync() {
        when(jobSite.getName()).thenReturn(summary.siteName());
        when(jobRepository.getEnabledJobsBySiteName(summary.siteName())).thenReturn(Flux.fromIterable(summary.jobs()));

        StepVerifier.create(jobSiteAlerter.checkSite())
                .expectNext(summary)
                .verifyComplete();

        verifyNoInteractions(alertNotifier1);
        verifyNoInteractions(alertNotifier2);
    }

    @Test
    void should_notify_weekly_summary() {
        when(alertNotifier1.alertWeeklySummary(summary)).thenReturn(Mono.empty());
        when(alertNotifier2.alertWeeklySummary(summary)).thenReturn(Mono.empty());

        StepVerifier.create(jobSiteAlerter.weeklySummary())
                .expectNext(summary)
                .verifyComplete();
        verify(alertNotifier1, times(1)).alertWeeklySummary(summary);
        verify(alertNotifier2, times(1)).alertWeeklySummary(summary);
    }
}