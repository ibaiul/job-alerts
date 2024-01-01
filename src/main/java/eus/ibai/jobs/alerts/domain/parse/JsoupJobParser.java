package eus.ibai.jobs.alerts.domain.parse;

import eus.ibai.jobs.alerts.domain.Job;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JsoupJobParser {

    private static final Pattern ARRAY_SELECTION_PATTERN = Pattern.compile("(.*)\\[(\\d+)\\]");

    public Flux<Job> parseJobs(String html, String steps, String siteUrl) {
        String[] stepValues = steps.split(",");
        String mainQuery = stepValues[0];
        String[] secondaryQueries = Arrays.copyOfRange(stepValues, 1, stepValues.length);
        return Mono.just(html)
                .map(Jsoup::parse)
                .map(document -> processQuery(new Elements(document), mainQuery))
                .flatMap(elements -> Flux.fromArray(secondaryQueries)
                        .reduce(elements, this::processQuery))
                .flatMapMany(Flux::fromIterable)
                .map(jobElement -> toJob(jobElement, siteUrl));
    }

    private Elements processQuery(Elements elements, String query) {
        Matcher matcher = ARRAY_SELECTION_PATTERN.matcher(query);
        if (matcher.matches()) {
            String actualQuery = matcher.group(1);
            int index = Integer.parseInt(matcher.group(2));
            Elements result = elements.select(actualQuery);
            if (result.size() <= index) {
                throw new ParsingException("Could not find element for Jsoup query: %s".formatted(query));
            }
            Element element = result.get(index);
            return new Elements(element);
        } else {
            return elements.select(query);
        }
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
            log.debug("Ignoring job url with relative path. Href: {}, Site: {}", href, siteUrl);
            return null;
        }
        return baseUrl(siteUrl).concat(href);
    }

    private String baseUrl(String siteUrl) {
        return siteUrl.substring(0, siteUrl.indexOf("/", 8));
    }
}
