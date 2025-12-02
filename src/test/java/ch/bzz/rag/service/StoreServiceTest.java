package ch.bzz.rag.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.document.Document;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StoreServiceTest {

    private final StoreService storeService;
    private final DocumentService documentService;

    @Test
    void testSaveAndSearch() {
        // Arrange
        Map<String, String> testData = new HashMap<>();
        testData.put("Hunde machen wau.", "https://de.wiktionary.org/wiki/wau");
        testData.put("Katzen machen miau.", "https://de.wiktionary.org/wiki/miau");
        testData.put("Kühe machen muh.", "https://de.wiktionary.org/wiki/muh");
        testData.put("Esel machen ia.", "https://de.wiktionary.org/wiki/ia");
        testData.put("Hähne machen kikeriki.", "https://de.wiktionary.org/wiki/kikeriki");
        testData.put("Ziegen machen mäh.", "https://de.wiktionary.org/wiki/m%C3%A4h");
        testData.put("Schafe machen mäh.", "https://de.wiktionary.org/wiki/m%C3%A4h");

        storeService.save(testData.entrySet().stream().map(
                entry -> documentService.createDocument(entry.getKey(), entry.getValue())
        ).toList());
        storeService.updateIndex();

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
