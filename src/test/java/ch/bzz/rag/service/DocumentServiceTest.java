package ch.bzz.rag.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ch.bzz.rag.service.DocumentService.DEFAULT_CHUNK_SIZE;
import static ch.bzz.rag.service.DocumentService.DEFAULT_CHUNK_OVERLAP;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DocumentServiceTest {

    private static final String EXAMPLE_SOURCE = "https://www.lipsum.com/feed/html";
    private static final String EXAMPLE_TEXT = """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec nec condimentum ligula, nec hendrerit dolor. Sed
pulvinar eros nulla. Phasellus velit magna, varius eu orci ut, pharetra mollis odio. Integer ornare consequat ligula
sed fringilla. Aliquam id leo odio. Cras in enim in tellus laoreet laoreet a eu mi. Nulla gravida aliquet mauris, et
venenatis dui interdum eget. Nam ac lectus dolor. Vestibulum ullamcorper tincidunt aliquam. Nunc auctor nunc vehicula,
semper mi id, blandit enim. Aliquam mattis quam libero, sit amet convallis dolor facilisis in. Aenean laoreet facilisis
nunc non aliquam. Etiam sit amet erat eget magna vestibulum mattis nec vitae velit. Nunc eget enim vel lacus ultricies
lacinia varius id magna. Nullam in quam tempor, ullamcorper augue elementum, viverra lectus. Sed dignissim purus
viverra, blandit turpis at, eleifend justo. Fusce accumsan neque purus, ut placerat sapien laoreet a. Aliquam eget
ornare justo. Donec porttitor urna et velit elementum consequat. Integer lacinia porta consequat. Phasellus augue
purus, imperdiet vel accumsan a, viverra a orci. Morbi dignissim non magna id aliquam. Suspendisse potenti. Duis
volutpat orci eu lacus sodales, non faucibus erat auctor. Duis venenatis est nec ex imperdiet varius. Etiam in feugiat
velit. Fusce feugiat nisl sit amet imperdiet tristique. Maecenas sit amet turpis tristique, placerat massa et, vehicula
ante. Integer posuere nibh sit amet placerat efficitur. Integer rhoncus velit vel velit bibendum, ac porta erat
tincidunt. Praesent id gravida neque, in fermentum nunc. Phasellus hendrerit interdum tellus, sed dignissim massa
efficitur sit amet. Pellentesque gravida velit et ipsum pulvinar euismod vel sed lorem. Nullam id ornare justo. Etiam
feugiat, leo in mattis fringilla, augue libero viverra erat, vitae sollicitudin mi nunc eu lacus. Pellentesque euismod,
libero quis vehicula aliquet, arcu nisi maximus risus, in dictum diam est ut massa. Suspendisse a blandit felis. Nulla
fermentum vitae tortor.""";

    private final DocumentService documentService;

    @Test
    void testChunkingCreatesCorrectChunks() {
        // Arrange
        // Act
        List<Document> docs = documentService.createDocuments(EXAMPLE_TEXT, EXAMPLE_SOURCE);

        // Assert
        assertThat(docs.getFirst().getMetadata().get("source")).isEqualTo(EXAMPLE_SOURCE);

        List<String> chunks = docs.stream().map(Document::getText).toList();
        assertThat(chunks.size()).isGreaterThanOrEqualTo(3);
        assertThat(chunks.getFirst().length()).isEqualTo(DEFAULT_CHUNK_SIZE);
        assertThat(chunks.getLast().length()).isLessThanOrEqualTo(DEFAULT_CHUNK_SIZE);

        String overlap = chunks.getFirst().substring(DEFAULT_CHUNK_SIZE - DEFAULT_CHUNK_OVERLAP);
        assertThat(chunks.get(1)).startsWith(overlap);

    }

    @Test
    void testIllegalArgument() {
        // Arrange
        // Act
        var thrown = assertThrows(IllegalArgumentException.class,
                () -> documentService.createDocuments(null, EXAMPLE_SOURCE)
        );
        // Assert
        assertThat(thrown.getMessage().contains("Invalid"));
    }
}
