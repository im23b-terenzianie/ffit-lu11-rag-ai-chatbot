package ch.bzz.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    public static final int DEFAULT_CHUNK_SIZE = 800;
    public static final int DEFAULT_CHUNK_OVERLAP = 80;

    public Document createDocument(String content, String source) {
        String id = UUID.nameUUIDFromBytes(content.getBytes(StandardCharsets.UTF_8)).toString();
        return new Document(id, content, Map.of("source", source));
    }

    public List<Document> createDocuments(String content, String source) {
        List<String> chunks = splitToChunks(content, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_OVERLAP);
        // return chunks.stream().map(chunk -> createDocument(chunk, source)).toList(); // More compact form
        List<Document> docs = new ArrayList<>();
        for (String chunk : chunks) {
            docs.add(createDocument(chunk, source));
        }
        return docs;
    }

    public List<String> splitToChunks(String text, int chunkSize, int overlap) {
        if (text == null || overlap < 0 || chunkSize <= overlap) {
            throw new IllegalArgumentException("Invalid text, chunkSize or overlap value.");
        }
        List<String> result = new ArrayList<>();

        // TODO: implement logic

        log.debug("Created {} chunks with chunkSize {} and overlap {}", result.size(), chunkSize, overlap);
        return result;
    }
}
