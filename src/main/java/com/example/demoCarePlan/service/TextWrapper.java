package com.example.demoCarePlan.service;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {

    public static List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null) return lines;
        String[] paragraphs = text.split("\\r?\\n");
        for (String para : paragraphs) {
            String[] words = para.split("\\s+");
            StringBuilder line = new StringBuilder();
            for (String w : words) {
                String test = line.length() == 0 ? w : line + " " + w;
                float width = calcWidth(font, fontSize, test);
                if (width <= maxWidth) {
                    if (line.length() > 0) line.append(" ");
                    line.append(w);
                } else {
                    if (line.length() > 0) {
                        lines.add(line.toString());
                        line = new StringBuilder(w);
                    } else {
                        //  força quebra de linha 
                        String part = forceSplitWord(w, font, fontSize, maxWidth);
                        lines.add(part);
                        String rest = w.substring(part.length());
                        line = new StringBuilder(rest);
                    }
                }
            }
            if (line.length() > 0) lines.add(line.toString());
        }
        return lines;
    }

    private static float calcWidth(PDFont font, float fontSize, String text) {
        try {
            return (font.getStringWidth(text) / 1000f) * fontSize;
        } catch (Exception e) {
            return text.length() * fontSize * 0.5f;
        }
    }

    private static String forceSplitWord(String word, PDFont font, float fontSize, float maxWidth) {
        StringBuilder sb = new StringBuilder();
        for (char c : word.toCharArray()) {
            sb.append(c);
            float w;
            try {
                w = (font.getStringWidth(sb.toString()) / 1000f) * fontSize;
            } catch (Exception e) {
                w = sb.length() * fontSize * 0.5f;
            }
            if (w > maxWidth) {
                sb.setLength(sb.length() - 1);
                break;
            }
        }
        return sb.toString();
    }
}