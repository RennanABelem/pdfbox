package com.example.demoCarePlan.service.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.example.demoCarePlan.service.FontManager;
import com.example.demoCarePlan.service.PdfDocumentBuilder;
import com.example.demoCarePlan.service.PdfDrawHelper;
import com.example.demoCarePlan.service.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;

public class MultiColumnRenderer {

    private final PdfDocumentBuilder builder;
    private final FontManager fontManager;
    private final float globalFontSize;

    public MultiColumnRenderer(PdfDocumentBuilder builder, FontManager fontManager, float globalFontSize) {
        this.builder = builder;
        this.fontManager = fontManager;
        this.globalFontSize = globalFontSize;
    }

    public void render(JsonNode block) throws Exception {
        JsonNode fields = block.path("fields");
        if (!fields.isArray() || fields.size() == 0) {
            builder.moveCursorBy(-PdfConstants.DEFAULT_LEADING);
            builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
            return;
        }

        float usableWidth = builder.getUsableWidth();
        float marginLeft = PdfDocumentBuilder.DEFAULT_MARGIN_LEFT;
        float colWidth = usableWidth / (float) Math.max(1, fields.size());

        List<List<String>> wrappedPerField = new ArrayList<>();
        List<Float> xPositions = new ArrayList<>();
        List<Float> availableWidths = new ArrayList<>();
        List<Float> fontSizes = new ArrayList<>();
        List<PDFont> fonts = new ArrayList<>();

        int maxWrappedLines = 0;

        for (int i = 0; i < fields.size(); i++) {
            JsonNode f = fields.get(i);
            if (f == null || "blank".equalsIgnoreCase(f.path("type").asText(""))) {
                wrappedPerField.add(new ArrayList<>());
                xPositions.add(null);
                availableWidths.add(colWidth - 4f);
                fontSizes.add(globalFontSize);
                fonts.add(null);
                continue;
            }

            String fontName = f.path("font").asText("DejaVuSans");
            PDFont font = fontManager.getFont(fontName);
            float fontSize = parseFloatOrDefault(f.path("font-size").asText(null), globalFontSize);

            float x;
            float availWidth;
            if (f.has("axis-x") && !f.path("axis-x").asText("").isBlank()) {
                x = marginLeft + parseFloatOrDefault(f.path("axis-x").asText(null), 0f);
                availWidth = usableWidth - (x - marginLeft);
            } else {
                x = marginLeft + i * colWidth + 2f;
                availWidth = colWidth - 4f;
            }

            List<String> wrapped = TextWrapper.wrapText(f.path("text-value").asText(""), font, fontSize, availWidth);
            maxWrappedLines = Math.max(maxWrappedLines, Math.max(1, wrapped.size()));

            wrappedPerField.add(wrapped);
            xPositions.add(x);
            availableWidths.add(availWidth);
            fontSizes.add(fontSize);
            fonts.add(font);
        }

        int rowsToDraw = Math.max(1, maxWrappedLines);
        for (int rowIndex = 0; rowIndex < rowsToDraw; rowIndex++) {
            builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
            float y = builder.getCursorY();

            for (int colIndex = 0; colIndex < fields.size(); colIndex++) {
                List<String> wrapped = wrappedPerField.get(colIndex);
                if (wrapped == null || wrapped.isEmpty()) continue;
                String rowText = rowIndex < wrapped.size() ? wrapped.get(rowIndex) : null;
                if (rowText == null) continue;

                PDFont font = fonts.get(colIndex);
                float usedFontSize = fontSizes.get(colIndex);
                Float x = xPositions.get(colIndex);
                float availWidth = availableWidths.get(colIndex);

                if (x == null || font == null) continue;

                PdfDrawHelper.drawTextLine(builder.getContentStream(), rowText, x, y, font, usedFontSize,
                        fields.get(colIndex).path("text-type").asText("left"), availWidth);
            }

            builder.moveCursorBy(-PdfConstants.DEFAULT_LEADING);
        }

        builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
    }

    private float parseFloatOrDefault(String value, float defaultValue) {
        try {
            if (value == null || value.isBlank()) return defaultValue;
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}