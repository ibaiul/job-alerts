package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.application.JobSiteProperties.JobSiteDefinition;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JobSitePropertiesTest {

    @Test
    void should_replace_underscore_characters_in_job_site_name() {
        String expectedName = "Job site name";
        JobSiteDefinition jobSiteDefinition = new JobSiteDefinition("Job_site_name", null, null, null);

        String actualName = jobSiteDefinition.name();

        assertThat(actualName, equalTo(expectedName));
    }
}