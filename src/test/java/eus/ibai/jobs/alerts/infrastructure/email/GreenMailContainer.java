package eus.ibai.jobs.alerts.infrastructure.email;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static java.lang.String.format;

public class GreenMailContainer<SELF extends GreenMailContainer<SELF>> extends GenericContainer<SELF> {

    private static final int SMTP_PORT = 3025;

    private static final int HTTP_PORT = 8080;

    private static final String GREENMAIL_OPTIONS = "-Dgreenmail.setup.test.all -Dgreenmail.users.login=email -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=%s:%s@%s";

    private String username;

    private String password;

    public GreenMailContainer(String dockerImageName) {
        super(DockerImageName.parse(dockerImageName));
//        this.waitStrategy = Wait.forHttp("/api/service/readiness")
//                .forPort(HTTP_PORT)
//                .withStartupTimeout(Duration.of(60, SECONDS));
        addExposedPort(SMTP_PORT);
        addExposedPort(HTTP_PORT);
    }

    public SELF withCredentials(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
        return self();
    }

    @Override
    protected void configure() {
        if (username != null && password != null) {
            String[] userDetails = username.split("@");
            addEnv("GREENMAIL_OPTS", format(GREENMAIL_OPTIONS, userDetails[0], password, userDetails[1]));
        }
    }

    public int getMappedSmtpPort() {
        return getMappedPort(SMTP_PORT);
    }

    public int getMappedHttpPort() {
        return getMappedPort(HTTP_PORT);
    }
}
