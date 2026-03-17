package com.sahil.ai.docragengine.service;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private PgVectorEmbeddingStore embeddingStore;

    @Autowired
    private ChatLanguageModel chatModel;

    public String ask(String question) {

        Embedding queryEmbedding =
                embeddingModel.embed(question).content();

        List<EmbeddingMatch<TextSegment>> matches =
                embeddingStore.findRelevant(queryEmbedding, 8);

        StringBuilder context = new StringBuilder();

        for (EmbeddingMatch<TextSegment> match : matches) {
            context.append(match.embedded().text()).append("\n");
        }

        String prompt = """
You are a document assistant.

Use ONLY the information provided in the context below.
If the answer is not present in the context, say:
"I could not find the answer in the document."

Context:
%s

Question:
%s

Answer clearly and concisely:
""".formatted(context.toString(), question);

        return chatModel.generate(prompt);
    }
}