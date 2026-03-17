package com.sahil.ai.docragengine.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
    @Profile("!test")
    public class RagConfig {

        @Value("${openai.api.key}")
        private String apiKey;


        @Bean
        public EmbeddingModel embeddingModel() {
            return OpenAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .modelName("text-embedding-3-small")
                    .build();
        }

        @Bean
        public ChatLanguageModel chatModel() {
            return OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName("gpt-4o-mini")
                    .build();
        }

        @Bean
        public PgVectorEmbeddingStore embeddingStore() {
            return PgVectorEmbeddingStore.builder()
                    .host("localhost")
                    .port(5433)
                    .database("ragdb")
                    .user("postgres")
                    .password("postgres")
                    .table("pdf_embeddings")
                    .dimension(1536)
                    .build();
        }
    }

