package ch.bzz.rag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final PgVectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public void save(List<Document> documents) {
        vectorStore.add(documents);
        log.debug("Saved {} documents into vector store", documents.size());
    }

    public List<Document> search(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();
        log.debug(searchRequest.toString());
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        log.info("Got {} documents from vector store", documents.size());
        documents.forEach(d -> {
            Double score = d.getScore();
            String content = d.getText();
            log.debug("Score: {}, Text: '{}'", score, content);
        });
        return documents;
    }

    public Integer count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class);
    }

    public void updateIndex() {
        jdbcTemplate.execute("ANALYZE vector_store;");
    }

    public List<String> getSourcesForNamespace(String namespaceUrl) {
        String sql = """
            SELECT metadata->>'source' AS source
            FROM vector_store
            WHERE metadata->>'source' LIKE ?
        """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setString(1, namespaceUrl + "%"),
                (rs, rowNum) -> rs.getString("source")
        );
    }
}
