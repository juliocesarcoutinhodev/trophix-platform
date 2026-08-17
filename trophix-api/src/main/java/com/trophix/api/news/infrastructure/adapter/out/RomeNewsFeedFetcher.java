package com.trophix.api.news.infrastructure.adapter.out;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.trophix.api.news.application.ports.out.NewsFeedFetcher;
import com.trophix.api.news.model.NewsFeedItem;
import com.trophix.api.news.model.NewsSource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches and parses RSS 1.0/2.0 and Atom feeds using ROME. Uses a plain
 * blocking {@link RestClient} — the app runs on virtual threads, so Loom takes
 * care of the I/O without reactive APIs.
 *
 * <p>Featured image extraction follows this fallback cascade:
 * <ol>
 *   <li>{@code <enclosure>} and {@code <media:content>};</li>
 *   <li>first {@code <img>} inside {@code content:encoded} / {@code description},
 *       discarding junk URLs (e.g. WordPress {@code s.w.org} emoji placeholders);</li>
 *   <li>last resort: a fast GET to the article URL and scrape
 *       {@code <meta property="og:image">} — the canonical share image.</li>
 * </ol>
 */
@Slf4j
@Component
public class RomeNewsFeedFetcher implements NewsFeedFetcher {

    private static final Pattern IMG_SRC_PATTERN = Pattern.compile(
            "(?i)<img\\b[^>]*?src\\s*=\\s*[\"']([^\"']+)[\"']");

    /** URL fragments that mark placeholder/junk images (WordPress emoji, smilies...). */
    private static final List<String> JUNK_IMAGE_FRAGMENTS = List.of(
            "s.w.org", "emoji", "wp-smiley", "data:image");

    /** Maximum entries kept per feed per refresh. */
    private static final int MAX_ITEMS = 25;

    private static final String USER_AGENT = "Trophix-NewsBot/1.0 (+RSS aggregator)";

    private final RestClient restClient;

    public RomeNewsFeedFetcher() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(15).toMillis());
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public List<NewsFeedItem> fetch(NewsSource source) {
        String xml = restClient.get()
                .uri(source.url())
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .retrieve()
                .body(String.class);

        if (xml == null || xml.isBlank()) {
            return List.of();
        }

        SyndFeed feed = parseFeed(xml, source);
        return feed.getEntries().stream()
                .limit(MAX_ITEMS)
                .map(this::toItem)
                .filter(Objects::nonNull)
                .toList();
    }

    private SyndFeed parseFeed(String xml, NewsSource source) {
        try (InputStream input = new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            return new SyndFeedInput().build(new XmlReader(input));
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Falha ao parsear o feed '" + source.name() + "': " + ex.getMessage(), ex);
        }
    }

    private NewsFeedItem toItem(SyndEntry entry) {
        String title = entry.getTitle() != null ? entry.getTitle().strip() : null;
        String link = entry.getLink() != null ? entry.getLink().strip() : null;
        if (title == null || title.isBlank() || link == null || link.isBlank()) {
            return null;
        }
        return new NewsFeedItem(title, link, extractImage(entry, link), toInstant(entry));
    }

    private Instant toInstant(SyndEntry entry) {
        Date date = entry.getPublishedDate();
        if (date == null) {
            date = entry.getUpdatedDate();
        }
        return date != null ? date.toInstant() : Instant.now();
    }

    // ---------------------------------------------------------------------
    // Featured image extraction (fallback cascade)
    // ---------------------------------------------------------------------

    private String extractImage(SyndEntry entry, String link) {
        String url = firstNonJunk(imageFromEnclosure(entry));
        if (url == null) {
            url = firstNonJunk(imageFromMediaContent(entry));
        }
        if (url == null) {
            url = firstNonJunk(imageFromContents(entry));
        }
        if (url == null) {
            url = firstNonJunk(imageFromDescription(entry));
        }
        if (url == null) {
            url = fetchOgImage(link);
        }
        return url;
    }

    private String firstNonJunk(String url) {
        return (url != null && !isJunkImage(url)) ? url : null;
    }

    private boolean isJunkImage(String url) {
        if (url == null || url.isBlank()) {
            return true;
        }
        String lower = url.toLowerCase(Locale.ROOT);
        return JUNK_IMAGE_FRAGMENTS.stream().anyMatch(lower::contains);
    }

    private String imageFromEnclosure(SyndEntry entry) {
        if (entry.getEnclosures() == null) {
            return null;
        }
        return entry.getEnclosures().stream()
                .filter(enclosure -> enclosure.getType() == null || enclosure.getType().startsWith("image/"))
                .map(SyndEnclosure::getUrl)
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String imageFromMediaContent(SyndEntry entry) {
        if (entry.getForeignMarkup() == null) {
            return null;
        }
        for (org.jdom2.Element element : entry.getForeignMarkup()) {
            if (element.getNamespace() == null) {
                continue;
            }
            if (!element.getNamespace().getURI().contains("media")) {
                continue;
            }
            if (!"content".equals(element.getName())) {
                continue;
            }
            String url = element.getAttributeValue("url");
            if (url != null && !url.isBlank()) {
                return url;
            }
        }
        return null;
    }

    /**
     * WordPress-style feeds (PlayStation Blog, MeuPlayStation) put the full
     * HTML with the featured image in {@code content:encoded}, exposed by ROME
     * through {@code SyndEntry#getContents()}.
     */
    private String imageFromContents(SyndEntry entry) {
        if (entry.getContents() == null) {
            return null;
        }
        for (SyndContent content : entry.getContents()) {
            String url = firstImageSrc(content.getValue());
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    private String imageFromDescription(SyndEntry entry) {
        if (entry.getDescription() == null) {
            return null;
        }
        return firstImageSrc(entry.getDescription().getValue());
    }

    private String firstImageSrc(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }
        Matcher matcher = IMG_SRC_PATTERN.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Last resort: fetches the article page and reads the canonical share image
     * from {@code <meta property="og:image">}. Failures are non-fatal — the item
     * is kept with a null image.
     */
    private String fetchOgImage(String articleUrl) {
        try {
            String html = restClient.get()
                    .uri(articleUrl)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .retrieve()
                    .body(String.class);
            if (html == null || html.isBlank()) {
                return null;
            }
            Document document = Jsoup.parse(html, articleUrl);
            Element meta = document.selectFirst(
                    "meta[property='og:image:secure_url'], meta[property='og:image'], meta[name='og:image']");
            if (meta == null) {
                return null;
            }
            String content = meta.attr("content");
            return content != null && !content.isBlank() ? content : null;
        } catch (Exception ex) {
            log.debug("og:image não encontrado para {}: {}", articleUrl, ex.getMessage());
            return null;
        }
    }
}
