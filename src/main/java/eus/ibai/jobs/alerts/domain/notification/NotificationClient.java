package eus.ibai.jobs.alerts.domain.notification;

import reactor.core.publisher.Flux;

import java.util.List;

public interface NotificationClient<T, S> {

    Flux<S> send(List<String> recipients, T message);
}
