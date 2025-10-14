package com.example.demoCarePlan.service.component;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.example.demoCarePlan.service.PdfDocumentBuilder;
import com.example.demoCarePlan.service.PdfDrawHelper;
import com.example.demoCarePlan.service.TextWrapper;
import com.fasterxml.jackson.databind.JsonNode;

public class SingleColumnRenderer {

    private final PdfDocumentBuilder builder;
    private final float globalFontSize;

    public SingleColumnRenderer(PdfDocumentBuilder builder, float globalFontSize) {
        this.builder = builder;
        this.globalFontSize = globalFontSize;
    }

    public void render(JsonNode node) throws Exception {
        String fontName = node.path("font").asText("Helvetica");
        PDFont font = loadFont(fontName);
        float fontSize = parseFloatOrDefault(node.path("font-size").asText(null), globalFontSize);

        float usableWidth = builder.getUsableWidth();
        float marginLeft = PdfDocumentBuilder.DEFAULT_MARGIN_LEFT;

        float xPosition;
        float availableWidth;
        if (node.has("axis-x") && !node.path("axis-x").asText("").isBlank()) {
            xPosition = marginLeft + parseFloatOrDefault(node.path("axis-x").asText(null), 0f);
            availableWidth = usableWidth - (xPosition - marginLeft) - 4f;
        } else {
            xPosition = marginLeft;
            availableWidth = usableWidth - 4f;
        }

        List<String> wrapped = TextWrapper.wrapText(
                node.path("text-value").asText(""),
                font,
                fontSize,
                availableWidth
        );

        for (String row : wrapped) {
            builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
            PdfDrawHelper.drawTextLine(
                    builder.getContentStream(),
                    row,
                    xPosition,
                    builder.getCursorY(),
                    font,
                    fontSize,
                    node.path("text-type").asText("left"),
                    availableWidth
            );
            builder.moveCursorBy(-PdfConstants.DEFAULT_LEADING);
        }

        builder.ensureSpace(PdfConstants.BOTTOM_MARGIN);
    }

    private float parseFloatOrDefault(String value, float defaultValue) {
        try {
            if (value == null || value.isBlank())
                return defaultValue;
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private PDFont loadFont(String fontName) {
        PDFont font;
        if ("Helvetica".equalsIgnoreCase(fontName)) {
            font = PDType1Font.HELVETICA;
        } else if ("Helvetica_Bold".equalsIgnoreCase(fontName)) {
            font = PDType1Font.HELVETICA_BOLD;
        } else {
            font = PDType1Font.HELVETICA;
        }
        return font;
    }
}
