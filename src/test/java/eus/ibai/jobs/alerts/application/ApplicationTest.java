package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.AcceptanceTest;
import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerter;
import eus.ibai.jobs.alerts.domain.alert.JobSiteAlerterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static eus.ibai.jobs.alerts.TestData.JOB_SITE_1_NAME;
import static eus.ibai.jobs.alerts.TestData.JOB_SITE_2_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ApplicationTest extends AcceptanceTest {

    @Autowired
    private JobSiteAlerterRegistry alerterRegistry;

    @Test
    void should_register_job_site_alerters_on_startup() {
        List<JobSiteAlerter> siteAlerters = alerterRegistry.getAll();

        assertThat(siteAlerters, containsInAnyOrder(
                hasProperty("siteName", equalTo(JOB_SITE_1_NAME)),
                hasProperty("siteName", equalTo(JOB_SITE_2_NAME)),
                hasProperty("siteName", equalTo(JOB_SITE_1_NAME + "-js"))
        ));
    }
}