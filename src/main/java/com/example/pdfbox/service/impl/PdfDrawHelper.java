package com.example.pdfbox.service.impl;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class PdfDrawHelper {

    public enum Alignment {
        LEFT, CENTER, RIGHT;

        public static Alignment from(String align) {
            if (align == null) return LEFT;
            return switch (align.toLowerCase()) {
                case "center" -> CENTER;
                case "right" -> RIGHT;
                default -> LEFT;
            };
        }
    }

    public static void drawTextLine(PDPageContentStream cs, String text,
                                    float x, float y,
                                    PDFont font, float fontSize,
                                    String align, float boxWidth) throws IOException {

        if (cs == null || font == null || text == null || text.isBlank()) return;

        Alignment alignment = Alignment.from(align);
        float textWidth = getTextWidth(text, font, fontSize);
        float drawX = calculateAlignedX(x, textWidth, boxWidth, alignment);

        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(drawX, y);
        cs.showText(text);
        cs.endText();
    }

    private static float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    private static float calculateAlignedX(float x, float textWidth, float boxWidth, Alignment align) {
        return switch (align) {
            case CENTER -> x + (boxWidth - textWidth) / 2f;
            case RIGHT -> x + (boxWidth - textWidth);
            case LEFT -> x;
        };
    }
}