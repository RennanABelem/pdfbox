package com.example.pdfbox.util;

import com.example.pdfbox.service.impl.PdfDocumentBuilder;
import com.example.pdfbox.service.impl.PdfDrawHelper;
import com.example.pdfbox.service.impl.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.ArrayList;
import java.util.List;

public class MultiColumnRenderer {

    private final PdfDocumentBuilder builder;
    private final float defaultFontSize;

    public MultiColumnRenderer(PdfDocumentBuilder builder, float defaultFontSize) {
        this.builder = builder;
        this.defaultFontSize = defaultFontSize;
    }

    public void render(JsonNode block) throws Exception {
        JsonNode fields = block.path("fields");
        if (!fields.isArray() || fields.isEmpty()) {
            builder.moveCursorBy(-PdfDocumentBuilder.DEFAULT_LEADING);
            builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
            return;
        }

        float usableWidth = builder.getUsableWidth();
        float marginLeft = PdfDocumentBuilder.MARGIN_LEFT;
        float colWidth = usableWidth / Math.max(1, fields.size());

        List<List<String>> wrappedTexts = new ArrayList<>();
        List<Float> xPositions = new ArrayList<>();
        List<Float> availableWidths = new ArrayList<>();
        List<Float> fontSizes = new ArrayList<>();
        List<PDFont> fonts = new ArrayList<>();

        int maxLines = 0;

        for (int i = 0; i < fields.size(); i++) {
            JsonNode field = fields.get(i);
            if (field == null || "blank".equalsIgnoreCase(field.path("type").asText(""))) {
                wrappedTexts.add(new ArrayList<>());
                xPositions.add(null);
                availableWidths.add(colWidth - 4f);
                fontSizes.add(defaultFontSize);
                fonts.add(null);
                continue;
            }

            PDFont font = resolveFont(field.path("font").asText("Helvetica"));
            float fontSize = parseFloatOrDefault(field.path("font-size").asText(null), defaultFontSize);

            float x = calculateXPosition(field, marginLeft, i, colWidth);
            float width = calculateAvailableWidth(field, usableWidth, x, marginLeft, colWidth);

            List<String> wrapped = TextWrapper.wrapText(field.path("text-value").asText(""), font, fontSize, width);
            maxLines = Math.max(maxLines, wrapped.size());

            wrappedTexts.add(wrapped);
            xPositions.add(x);
            availableWidths.add(width);
            fontSizes.add(fontSize);
            fonts.add(font);
        }

        for (int row = 0; row < maxLines; row++) {
            builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
            float y = builder.getCursorY();

            for (int col = 0; col < fields.size(); col++) {
                List<String> wrapped = wrappedTexts.get(col);
                if (wrapped == null || wrapped.isEmpty()) continue;

                String text = row < wrapped.size() ? wrapped.get(row) : null;
                if (text == null) continue;

                PDFont font = fonts.get(col);
                Float x = xPositions.get(col);
                float fontSize = fontSizes.get(col);
                float width = availableWidths.get(col);

                if (x == null || font == null) continue;

                PdfDrawHelper.drawTextLine(
                        builder.getContentStream(),
                        text,
                        x,
                        y,
                        font,
                        fontSize,
                        fields.get(col).path("text-type").asText("left"),
                        width
                );
            }

            builder.moveCursorBy(-PdfDocumentBuilder.DEFAULT_LEADING);
        }

        builder.ensureSpace(PdfDocumentBuilder.MARGIN_BOTTOM);
    }

    private float calculateXPosition(JsonNode field, float marginLeft, int index, float colWidth) {
        if (field.has("axis-x") && !field.path("axis-x").asText("").isBlank()) {
            return marginLeft + parseFloatOrDefault(field.path("axis-x").asText(null), 0f);
        }
        return marginLeft + index * colWidth + 2f;
    }

    private float calculateAvailableWidth(JsonNode field, float usableWidth, float x, float marginLeft, float colWidth) {
        if (field.has("axis-x") && !field.path("axis-x").asText("").isBlank()) {
            return usableWidth - (x - marginLeft);
        }
        return colWidth - 4f;
    }

    private PDFont resolveFont(String fontName) {
        return switch (fontName.toLowerCase()) {
            case "helvetica_bold" -> PDType1Font.HELVETICA_BOLD;
            case "helvetica" -> PDType1Font.HELVETICA;
            default -> PDType1Font.HELVETICA;
        };
    }

    private float parseFloatOrDefault(String value, float defaultValue) {
        try {
            return (value == null || value.isBlank()) ? defaultValue : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}