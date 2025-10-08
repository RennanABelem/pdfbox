package com.example.demoCarePlan.service.component;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.example.demoCarePlan.service.FontManager;
import com.example.demoCarePlan.service.PdfDocumentBuilder;
import com.fasterxml.jackson.databind.JsonNode;

public class SignatureRenderer {

    private final PdfDocumentBuilder builder;
    private final FontManager fontManager;
    private final float globalFontSize;

    public SignatureRenderer(PdfDocumentBuilder builder, FontManager fontManager, float globalFontSize) {
        this.builder = builder;
        this.fontManager = fontManager;
        this.globalFontSize = globalFontSize;
    }

    public void render(JsonNode element) throws Exception {
    	
        PDPageContentStream cs = builder.getContentStream();

        String text = element.has("text") ? element.get("text").asText() : "";
        float blockWidth = element.has("width") ? (float) element.get("width").asDouble() : 260f;
        String fontName = element.has("font") ? element.get("font").asText() : "DejaVuSans";
        float fontSize = element.has("font-size") ? (float) element.get("font-size").asDouble() : globalFontSize;

        float marginBottom = PdfConstants.BOTTOM_MARGIN;
        float lineHeightAboveBottom = 18f;
        float distanceLineToText = 8f;
        float lineThickness = 0.5f;

        float startX = element.has("axis-x") ? (float) element.get("axis-x").asDouble() : 50f;
        float lineY = marginBottom + lineHeightAboveBottom;

        builder.ensureSpace(lineHeightAboveBottom + fontSize);

        PDFont font = fontManager.getFont(fontName);
        
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

        builder.moveCursorBy(- (lineHeightAboveBottom + fontSize + distanceLineToText));
    }
}
