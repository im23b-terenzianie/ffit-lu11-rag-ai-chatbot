package ch.bzz.rag.service;

import ch.bzz.rag.factory.DocumentFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.document.Document;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StoreServiceTest {

    private final StoreService storeService;
    private final DocumentFactory docFac;

    @Test
    void testSaveAndSearch() {
        // Arrange
        List<Document> docs = new ArrayList<>();
        docs.add(docFac.create("Hunde machen wau.", "https://de.wiktionary.org/wiki/wau"));
        docs.add(docFac.create("Katzen machen miau.", "https://de.wiktionary.org/wiki/miau"));
        docs.add(docFac.create("Kühe machen muh.", "https://de.wiktionary.org/wiki/muh"));
        docs.add(docFac.create("Esel machen ia.", "https://de.wiktionary.org/wiki/ia"));
        docs.add(docFac.create("Hähne machen kikeriki.", "https://de.wiktionary.org/wiki/kikeriki"));
        docs.add(docFac.create("Ziegen machen mäh.", "https://de.wiktionary.org/wiki/m%C3%A4h"));
        docs.add(docFac.create("Schafe machen mäh.", "https://de.wiktionary.org/wiki/m%C3%A4h"));
        storeService.save(docs);

        // Act
        int numberOfResults = 3;
        String query = "Welchen Laut machen Katzen?";
        List<Document> result = storeService.search(query, numberOfResults);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(numberOfResults);
        assertThat(result.getFirst().getText()).contains("miau");
        assertThat(result.getFirst().getMetadata().get("source").toString()).contains("miau");
    }
}
