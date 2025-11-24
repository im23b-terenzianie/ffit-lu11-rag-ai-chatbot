package ch.bzz.rag.init;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

@Component
@RequiredArgsConstructor
public class VectorTableInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    @Override
    public void run(String... args) {
        int dimensions = embeddingModel.dimensions();
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector;");
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vector_store (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                content TEXT NOT NULL,
                embedding vector(%d),
                metadata JSONB
            );
        """.formatted(dimensions));
        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS vector_store_embedding_idx
            ON vector_store
            USING ivfflat (embedding vector_cosine_ops)
            WITH (lists = 100);
        """);
    }
}
