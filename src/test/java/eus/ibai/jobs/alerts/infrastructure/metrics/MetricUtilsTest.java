package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static eus.ibai.jobs.alerts.TestData.JOB_SITE_1_NAME;
import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricUtils.recordActiveJobs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class MetricUtilsTest {

    @Test
    void should_register_active_jobs_when_recording_jobs_for_a_site() {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        int expectedActiveJobs = 22;

        recordActiveJobs(meterRegistry, JOB_SITE_1_NAME, expectedActiveJobs);

        Gauge activeJobsGauge = meterRegistry.find("jobs.active").tag("site_name", JOB_SITE_1_NAME).gauge();
        assertThat(activeJobsGauge, notNullValue());
        assertThat(activeJobsGauge.value(), equalTo((double) expectedActiveJobs));
    }

    @ParameterizedTest
    @MethodSource("provideSiteNames")
    void should_remove_whitespaces_when_transforming_site_name_into_tag(String siteName, String expectedTagName) {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();

        recordActiveJobs(meterRegistry, siteName, 1);

        Gauge activeJobsGauge = meterRegistry.find("jobs.active").tag("site_name", expectedTagName).gauge();
        assertThat(activeJobsGauge, notNullValue());
    }

    private static Stream<Arguments> provideSiteNames() {
        return Stream.of(
                Arguments.of("SiteNameWithNoSpaces", "SiteNameWithNoSpaces"),
                Arguments.of("Site Name With One Space", "Site_Name_With_One_Space"),
                Arguments.of("Site  Name    With      Multiple  Spaces", "Site_Name_With_Multiple_Spaces"),
                Arguments.of("Site\nName", "Site_Name"),
                Arguments.of("Site\r\nName", "Site_Name"),
                Arguments.of("Site\sName", "Site_Name"),
                Arguments.of("Site\fName", "Site_Name"),
                Arguments.of("Site\tName", "Site_Name"));
    }
}