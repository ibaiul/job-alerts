package eus.ibai.jobs.alerts.domain.notification;

import java.util.List;

public interface AlertNotifierFactory {

    NotifierType getNotifierType();

    AlertNotifier create(List<String> recipients);
}
