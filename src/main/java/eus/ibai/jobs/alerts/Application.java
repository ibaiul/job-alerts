package eus.ibai.jobs.alerts;

import eus.ibai.jobs.alerts.application.MainScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("local")
    public CommandLineRunner run(MainScheduler mainScheduler, ThreadPoolTaskScheduler springTaskScheduler) {
        return args -> {
            Stream.of(args).forEach(arg -> log.debug("APPLICATION ARG: {}", arg));
            log.debug("Running periodic schedule manually.");
            springTaskScheduler.schedule(mainScheduler::runPeriodicSchedule, Instant.now().plus(10, SECONDS));
        };
    }
}
