package ch.bzz.rag.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.document.Document;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StoreServiceTest {

    private final StoreService storeService;
    private final DocumentService documentService;

    @Test
    void testSaveAndSearch() {
        // Arrange
        List<Document> docs = new ArrayList<>();
        docs.add(documentService.createDocument("Hunde machen wau.", "https://de.wiktionary.org/wiki/wau"));
        docs.add(documentService.createDocument("Katzen machen miau.", "https://de.wiktionary.org/wiki/miau"));
        docs.add(documentService.createDocument("Kühe machen muh.", "https://de.wiktionary.org/wiki/muh"));
        docs.add(documentService.createDocument("Esel machen ia.", "https://de.wiktionary.org/wiki/ia"));
        docs.add(documentService.createDocument("Hähne machen kikeriki.", "https://de.wiktionary.org/wiki/kikeriki"));
        docs.add(documentService.createDocument("Ziegen machen mäh.", "https://de.wiktionary.org/wiki/m%C3%A4h"));
        docs.add(documentService.createDocument("Schafe machen mäh.", "https://de.wiktionary.org/wiki/m%C3%A4h"));
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
