package ch.bzz.rag.factory;

import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Component
public class DocumentFactory {

    public Document create(String content, String source) {
        String id = UUID.nameUUIDFromBytes(content.getBytes(StandardCharsets.UTF_8)).toString();
        return new Document(id, content, Map.of("source", source));
    }
}
