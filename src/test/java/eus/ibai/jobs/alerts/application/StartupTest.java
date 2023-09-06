package eus.ibai.jobs.alerts.application;

import eus.ibai.jobs.alerts.Application;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StartupTest {

    @Test
    void application_starts() {
        assertDoesNotThrow(() -> Application.main(new String[0]));
    }
}
