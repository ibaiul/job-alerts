package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import eus.ibai.jobs.alerts.infrastructure.jsoup.JsoupClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Slf4j
@Component
@AllArgsConstructor
public class BasicHtmlParsingStrategy implements JobParsingStrategy {

    public static final String TYPE = "basicHtml";

    private final JsoupClient jsoupClient;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Flux<Job> parseJobs(String siteUrl, String steps) {
        String[] stepValues = steps.split(",");
        String mainQuery = stepValues[0];
        String[] secondaryQueries = Arrays.copyOfRange(stepValues, 1, stepValues.length);
        return jsoupClient.parse(siteUrl)
                .map(document -> document.select(mainQuery))
                .flatMap(elements -> Flux.fromArray(secondaryQueries)
                                        .reduce(elements, Elements::select))
                .flatMapMany(Flux::fromIterable)
                .map(jobElement -> toJob(jobElement, siteUrl));
    }

    private Job toJob(Element htmlElement, String siteUrl) {
        String jobUrl = htmlElement.hasAttr("href") ? buildJobUrl(htmlElement.attr("href"), siteUrl) : null;
        return new Job(removeHtmlTags(htmlElement.text()), jobUrl);
    }

    private String removeHtmlTags(String text) {
        return text.replaceAll("<[^>]*>", "");
    }

    private String buildJobUrl(String href, String siteUrl) {
        if (href.startsWith("http")) {
            return href;
        } else if (!href.startsWith("/")) {
            log.warn("Malformed job url found. Href: {}, Site: {}", href, siteUrl);
            return null;
        }
        return baseUrl(siteUrl).concat(href);
    }

    private String baseUrl(String siteUrl) {
        return siteUrl.substring(0, siteUrl.indexOf("/", 8));
    }
}
