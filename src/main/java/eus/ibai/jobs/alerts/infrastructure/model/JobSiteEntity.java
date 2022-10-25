package eus.ibai.jobs.alerts.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("job_site")
@NoArgsConstructor
@AllArgsConstructor
public class JobSiteEntity {

    @Id
    private Long id;
    private String name;
    private String url;
    private String parsingStrategyType;
    private String parsingStrategySteps;
}
