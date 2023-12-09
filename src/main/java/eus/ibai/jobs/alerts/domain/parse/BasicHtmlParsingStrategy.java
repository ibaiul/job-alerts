package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.jsoup.BasicHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class BasicHtmlParsingStrategy implements JobParsingStrategy {

    public static final String TYPE = "basicHtml";

    private final String steps;

    private final BasicHttpClient basicHttpClient;

    private final JsoupJobParser jsoupJobParser;

    @Override
    public Flux<Job> parseJobs(String siteUrl) {
        return basicHttpClient.parse(siteUrl)
                .doOnNext(html -> log.trace("HTML response from {}: {}", siteUrl, html))
                .flatMapMany(html -> jsoupJobParser.parseJobs(html, steps, siteUrl));
    }
}
