package eus.ibai.jobs.alerts.domain.alert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSiteAlerterRegistryTest {

    @Mock
    private JobSiteAlerter alerter;

    @InjectMocks
    private JobSiteAlerterRegistry alerterRegistry;

    @Test
    void should_add_alerter_to_the_registry() {
        when(alerter.getSiteName()).thenReturn("jobSite1");

        alerterRegistry.registerJobSiteAlerter(alerter);

        List<JobSiteAlerter> alerters = alerterRegistry.getAll();
        assertThat(alerters, equalTo(List.of(alerter)));
    }

}