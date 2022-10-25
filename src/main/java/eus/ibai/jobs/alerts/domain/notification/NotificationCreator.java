package eus.ibai.jobs.alerts.domain.notification;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.domain.JobSiteSummary;

import java.util.List;

public interface NotificationCreator<T> {

    T createSiteUpdatedNotification(JobSiteSummary jobSiteSummary, List<Job> previousJobs);

    T createWeeklySummaryNotification(JobSiteSummary jobSiteSummary);
}
