package eus.ibai.jobs.alerts.domain.repository;

import eus.ibai.jobs.alerts.domain.JobSite;
import eus.ibai.jobs.alerts.domain.parse.BasicHtmlParsingStrategy;
import eus.ibai.jobs.alerts.infrastructure.jsoup.JsoupClient;
import eus.ibai.jobs.alerts.infrastructure.model.JobSiteEntity;
import eus.ibai.jobs.alerts.infrastructure.repository.JobSiteEntityRepository;
import eus.ibai.jobs.alerts.infrastructure.repository.JobSiteRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSiteRepositoryTest {

    @Mock
    private JobSiteEntityRepository jobSiteEntityRepository;

    @Mock
    private JsoupClient jsoupClient;

    private JobSiteRepository jobSiteRepository;

    @BeforeEach
    void beforeEach() {
        jobSiteRepository = new JobSiteRepositoryImpl(jobSiteEntityRepository);
    }

    @Test
    void should_persist_site_when_synchronising_and_does_not_exist() {
        String siteName = "jobSiteName";
        when(jobSiteEntityRepository.findByName(siteName)).thenReturn(Mono.empty());
        String siteUrl = "url";
        String parsingStrategySteps = "psSteps";
        BasicHtmlParsingStrategy parsingStrategy = new BasicHtmlParsingStrategy(jsoupClient);
        JobSite jobSite = new JobSite(siteName, siteUrl, parsingStrategy, parsingStrategySteps);
        JobSiteEntity expectedJobSiteEntity = new JobSiteEntity(null, siteName, siteUrl, parsingStrategy.getType(), parsingStrategySteps);
        when(jobSiteEntityRepository.save(expectedJobSiteEntity)).thenReturn(Mono.empty());

        StepVerifier.create(jobSiteRepository.sync(jobSite))
                .expectNext(jobSite)
                .verifyComplete();

        verify(jobSiteEntityRepository, times(1)).save(expectedJobSiteEntity);
    }

    @Test
    void should_update_site_when_synchronising_and_has_changed() {
        String siteName = "jobSiteName";
        BasicHtmlParsingStrategy parsingStrategy = new BasicHtmlParsingStrategy(jsoupClient);
        JobSiteEntity jobSiteEntity = new JobSiteEntity(1L, siteName, "url", parsingStrategy.getType(), "psSteps1");
        when(jobSiteEntityRepository.findByName(siteName)).thenReturn(Mono.just(jobSiteEntity));
        String newSiteUrl = "newUrl";
        String newParsingStrategySteps = "psSteps2";
        JobSite updatedJobSite = new JobSite(siteName, newSiteUrl, parsingStrategy, newParsingStrategySteps);
        JobSiteEntity updatedJobSiteEntity = new JobSiteEntity(1L, siteName, newSiteUrl, BasicHtmlParsingStrategy.TYPE, newParsingStrategySteps);
        when(jobSiteEntityRepository.save(updatedJobSiteEntity)).thenReturn(Mono.empty());

        StepVerifier.create(jobSiteRepository.sync(updatedJobSite))
                .expectNext(updatedJobSite)
                .verifyComplete();

        verify(jobSiteEntityRepository, times(1)).save(updatedJobSiteEntity);
    }

    @Test
    void should_not_update_site_when_synchronising_and_has_not_changed() {
        String siteName = "jobSiteName";
        String siteUrl = "url";
        String parsingStrategySteps = "psSteps";
        BasicHtmlParsingStrategy parsingStrategy = new BasicHtmlParsingStrategy(jsoupClient);
        JobSiteEntity jobSiteEntity = new JobSiteEntity(1L, siteName, siteUrl, parsingStrategy.getType(), parsingStrategySteps);
        when(jobSiteEntityRepository.findByName(siteName)).thenReturn(Mono.just(jobSiteEntity));
        JobSite jobSite = new JobSite(siteName, siteUrl, parsingStrategy, parsingStrategySteps);

        StepVerifier.create(jobSiteRepository.sync(jobSite))
                .expectNext(jobSite)
                .verifyComplete();

        verify(jobSiteEntityRepository, times(0)).save(any());
    }
}