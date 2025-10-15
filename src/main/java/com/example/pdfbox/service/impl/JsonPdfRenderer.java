package com.example.pdfbox.service.impl;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.example.pdfbox.util.MultiColumnRenderer;
import com.example.pdfbox.util.SignatureRenderer;
import com.example.pdfbox.util.SingleColumnRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonPdfRenderer {

    public byte[] render(JsonNode root) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PdfDocumentBuilder builder = new PdfDocumentBuilder(doc);

            float globalFontSize = parseFloatOrDefault(
                    root.path("font-size").asText(null),
                    PdfDocumentBuilder.DEFAULT_FONT_SIZE
            );

            JsonNode lines = root.path("lines");
            if (!lines.isArray()) {
                lines = JsonNodeFactory.instance.arrayNode(); 
            }

            SingleColumnRenderer singleRenderer = new SingleColumnRenderer(builder, globalFontSize);
            MultiColumnRenderer multiRenderer = new MultiColumnRenderer(builder, globalFontSize);
            SignatureRenderer signatureRenderer = new SignatureRenderer(builder, globalFontSize);

            for (JsonNode line : lines) {
                String type = line.path("type").asText("");

                switch (type.toLowerCase()) {
                    case "blank" -> {
                        builder.moveCursorBy(-PdfDocumentBuilder.DEFAULT_LEADING);
                        builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
                    }
                    case "multi-column" -> multiRenderer.render(line);
                    case "signature" -> signatureRenderer.render(line);
                    default -> singleRenderer.render(line); // fallback para single-column
                }
            }

            builder.getContentStream().close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private float parseFloatOrDefault(String value, float defaultValue) {
        try {
            return (value == null || value.isBlank()) ? defaultValue : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}