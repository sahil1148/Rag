package com.sahil.ai.docragengine.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfLoaderService {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final OpenAiEmbeddingModel embeddingModel;

    public PdfLoaderService(EmbeddingStore<TextSegment> embeddingStore,
                            OpenAiEmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void loadPdf() throws Exception {

        System.out.println("=== INDEXING PDF ===");

        File file = new ClassPathResource(
                "docs/LG Electronics India Limited - RHP.pdf"
        ).getFile();

        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        System.out.println("Extracted text length: " + text.length());

        if (text.isBlank()) {
            throw new RuntimeException("PDF has no readable text");
        }

        // 1️⃣ Chunk into 1000 character blocks
        List<TextSegment> segments = new ArrayList<>();

        int chunkSize = 1000;
        for (int i = 0; i < text.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, text.length());
            segments.add(TextSegment.from(text.substring(i, end)));
        }

        System.out.println("Total chunks: " + segments.size());

        // 2️⃣ Embed in small batches (avoid 300k token error)
        int batchSize = 10;

        for (int i = 0; i < segments.size(); i += batchSize) {
            int end = Math.min(i + batchSize, segments.size());
            List<TextSegment> batch = segments.subList(i, end);

            var embeddings = embeddingModel.embedAll(batch).content();

            embeddingStore.addAll(embeddings, batch);

            System.out.println("Embedded batch " + (i / batchSize + 1));
        }

        System.out.println("=== PDF INDEXED SUCCESSFULLY ===");
    }
}