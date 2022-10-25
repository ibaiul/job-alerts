package eus.ibai.jobs.alerts.infrastructure.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class DatabaseConfig {

    private final String url;

    private final String user;

    private final String pass;

    public DatabaseConfig(@Value("${spring.flyway.url}") String url, @Value("${spring.flyway.user}") String user, @Value("${spring.flyway.password}") String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        FluentConfiguration flywayConfiguration = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(url, user, pass);
        return new Flyway(flywayConfiguration);
    }
}

