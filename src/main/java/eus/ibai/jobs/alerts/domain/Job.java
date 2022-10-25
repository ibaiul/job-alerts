package eus.ibai.jobs.alerts.domain;

import lombok.Value;

@Value
public class Job {

    String title;

    String url;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
