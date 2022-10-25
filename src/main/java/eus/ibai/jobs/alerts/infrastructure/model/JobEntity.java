package eus.ibai.jobs.alerts.infrastructure.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("job")
public class JobEntity {

    @Id
    private Long id;
    private String title;
    private String url;
    @Column("created_on")
    private LocalDateTime createdOn;
    @Column("updated_on")
    private LocalDateTime updatedOn;
    private boolean enabled;
    // In order to get relational capabilities there are a few possibilities
    // - Hibernate Reactive API + mutiny-reactor
    // - BaseJobEntityRepository interface and custom implementation with DatabaseClient injected
    @Column("site_id")
    private Long siteId;

    protected JobEntity() {}

    public JobEntity(String title, String url, boolean enabled, Long siteId) {
        this.title = title;
        this.url = url;
        this.enabled = enabled;
        this.siteId = siteId;
        this.createdOn = LocalDateTime.now();
        this.updatedOn = this.createdOn;
    }
}
