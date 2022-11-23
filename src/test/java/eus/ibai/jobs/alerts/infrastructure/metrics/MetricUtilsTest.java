package eus.ibai.jobs.alerts.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static eus.ibai.jobs.alerts.TestData.JOB_SITE_1_NAME;
import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricTestUtils.verifyActiveJobsRecorded;
import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricUtils.clearGaugeReferences;
import static eus.ibai.jobs.alerts.infrastructure.metrics.MetricUtils.recordActiveJobs;

class MetricUtilsTest {

    private MeterRegistry meterRegistry;

    @BeforeEach
    void beforeEach() {
        clearGaugeReferences();
        meterRegistry = new SimpleMeterRegistry();
    }

    @Test
    void should_register_active_jobs_when_recording_jobs_for_a_site() {
        int expectedActiveJobs = 22;

        recordActiveJobs(meterRegistry, JOB_SITE_1_NAME, expectedActiveJobs);

        verifyActiveJobsRecorded(meterRegistry, JOB_SITE_1_NAME, expectedActiveJobs);
    }

    @Test
    void should_update_active_jobs_when_recording_jobs_for_a_site_again() {
        recordActiveJobs(meterRegistry, JOB_SITE_1_NAME, 1);
        int expectedActiveJobs = 2;

        recordActiveJobs(meterRegistry, JOB_SITE_1_NAME, expectedActiveJobs);

        verifyActiveJobsRecorded(meterRegistry, JOB_SITE_1_NAME, expectedActiveJobs);
    }

    @ParameterizedTest
    @MethodSource("provideSiteNamesWithWhitespaces")
    void should_replace_whitespaces_when_transforming_site_name_into_tag(String siteName, String expectedTagName) {
        int expectedActiveJobs = 7;

        recordActiveJobs(meterRegistry, siteName, expectedActiveJobs);

        verifyActiveJobsRecorded(meterRegistry, expectedTagName, expectedActiveJobs);
    }

    private static Stream<Arguments> provideSiteNamesWithWhitespaces() {
        return Stream.of(
                Arguments.of("Site Name With One Space", "Site_Name_With_One_Space"),
                Arguments.of("Site  Name    With      Multiple  Spaces", "Site_Name_With_Multiple_Spaces"),
                Arguments.of("Site\nName", "Site_Name"),
                Arguments.of("Site\r\nName", "Site_Name"),
                Arguments.of("Site\sName", "Site_Name"),
                Arguments.of("Site\fName", "Site_Name"),
                Arguments.of("Site\tName", "Site_Name"));
    }
}