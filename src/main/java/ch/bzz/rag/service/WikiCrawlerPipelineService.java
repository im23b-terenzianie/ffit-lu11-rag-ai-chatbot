package ch.bzz.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WikiCrawlerPipelineService {

    private final WikiPageCollectorService collectorService;
    private final WikiPageDownloaderService downloaderService;
    private final DocumentService documentService;
    private final StoreService storeService;

    public void runPipeline(String namespaceUrl, String regexFilter, boolean overwriteExisting) {
        log.info("Start crawler pipeline for url: {}", namespaceUrl);
        try {
            String namespace = getNamespaceFromUrl(namespaceUrl);
            String baseUrl = getDomainFromUrl(namespaceUrl);
            log.info("Using baseUrl {} and namespace {}", baseUrl, namespace);

            Set<String> collectedPages = collectorService.collectPagesForNamespace(baseUrl, namespace);

            if(null != regexFilter){
                collectedPages = collectedPages.stream()
                        .filter(collectedUrl -> collectedUrl.contains(namespaceUrl)) // Filter root pages
                        .filter(collectedUrl -> collectedUrl.matches(regexFilter))
                        .collect(Collectors.toSet());
            }

            if(!overwriteExisting){
                List<String> existingUrls = storeService.getSourcesForNamespace(namespaceUrl);
                log.info("Number of already stored documents: {}", existingUrls.size());
                collectedPages = collectedPages.stream()
                        .filter(collectedUrl -> !existingUrls.contains(collectedUrl))
                        .collect(Collectors.toSet());
            }
            log.info("{} pages after filtering", collectedPages.size());

            downloaderService.init(baseUrl);
            for (String collectedPage : collectedPages) {
                String pageId = getNamespaceFromUrl(collectedPage);
                String content = downloaderService.downloadPage(pageId);
                if (content != null) {
                    List<Document> docs = documentService.createDocuments(content, collectedPage);
                    storeService.save(docs);
                }
            }
            storeService.updateIndex();
        } catch (MalformedURLException e) {
            log.error("Error using url {}: {}", namespaceUrl, e.getMessage(), e);
        }
        log.info("End crawler pipeline for url: {}", namespaceUrl);
    }

    private String getNamespaceFromUrl(String namespaceUrl) throws MalformedURLException {
        URL url = URI.create(namespaceUrl).toURL();
        return url.getPath().substring(1).replace("/", ":");
    }

    private String getDomainFromUrl(String url){
        return url.substring(0, url.indexOf("/", url.indexOf(".")));
    }
}
