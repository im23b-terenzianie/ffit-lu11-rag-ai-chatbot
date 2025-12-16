package ch.bzz.rag.service;

import ch.bzz.rag.chat.ChatMode;
import ch.bzz.rag.doubles.ChatModelFake;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ch.bzz.rag.service.ChatService.ChatAnswer;
import ch.bzz.rag.service.ChatService.SourceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ChatServiceTest {

    private final StoreService storeService;

    @Test
    void testChatStandard() {
        // Arrange
        ChatModelFake chatModel = new ChatModelFake("Katzen machen den Laut miau. [DOC_1]");
        ChatService chatService = new ChatService(chatModel, storeService);

        // Act
        String query = "Welchen Laut machen Katzen?";
        ChatAnswer answer = chatService.chat(ChatMode.STANDARD, query);

        // Assert
        assertThat(answer.text()).contains("miau").contains("[DOC_1]");
        assertThat(answer.sources()).hasSize(1);
        SourceReference source = answer.sources().getFirst();
        assertThat(source.docIds()).hasSize(1);
        assertThat(source.docIds()).contains("DOC_1");
        assertThat(source.source()).contains("wiktionary.org/wiki/miau");
    }
}
