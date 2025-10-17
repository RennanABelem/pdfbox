package com.example.pdfbox.util;

import com.example.pdfbox.service.impl.PdfDocumentBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class SignatureRenderer {

    private final PdfDocumentBuilder builder;
    private final float globalFontSize;

    public SignatureRenderer(PdfDocumentBuilder builder, float globalFontSize) {
        this.builder = builder;
        this.globalFontSize = globalFontSize;
    }

    public void render(JsonNode element) throws Exception {
        PDPageContentStream cs = builder.getContentStream();

        String text = element.path("text").asText("");
        float blockWidth = (float) element.path("width").asDouble(260f);
        String fontName = element.path("font").asText("Helvetica");
        float fontSize = (float) element.path("font-size").asDouble(globalFontSize);
        float startX = (float) element.path("axis-x").asDouble(40f);

        float lineHeightAboveBottom = 18f;
        float distanceLineToText = 8f;
        float lineThickness = 0.5f;
        float marginBottom = PdfDocumentBuilder.MARGIN_BOTTOM;
        float lineY = marginBottom + lineHeightAboveBottom;

        builder.ensureSpace(lineHeightAboveBottom + fontSize);

        PDFont font = resolveFont(fontName);

        cs.setLineWidth(lineThickness);
        cs.moveTo(startX, lineY);
        cs.lineTo(startX + blockWidth, lineY);
        cs.stroke();

        float textY = lineY - distanceLineToText - fontSize;
        float textX = startX;

        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(textX, textY);
        cs.showText(text);
        cs.endText();

        builder.moveCursorBy(-(lineHeightAboveBottom + fontSize + distanceLineToText));
    }

    private PDFont resolveFont(String fontName) {
        return switch (fontName.toLowerCase()) {
            case "helvetica_bold" -> PDType1Font.HELVETICA_BOLD;
            case "helvetica" -> PDType1Font.HELVETICA;
            default -> PDType1Font.HELVETICA;
        };
    }
}