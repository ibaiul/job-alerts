package eus.ibai.jobs.alerts.domain;

import java.util.List;

public record JobSiteSummary (String siteName, String url, List<Job> jobs) {}
